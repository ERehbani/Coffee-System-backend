package com.CoffeeGroup.Coffee.System.features.redemption;

import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.ClientRepository;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionCreateMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionResponseMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RedemptionService implements IRedemptionService {

    private final IRedemptionRepository redemptionRepository;
    private final IRedemptionResponseMapper responseMapper;
    private final IRedemptionCreateMapper createMapper;
    private final ClientRepository clientRepository;
    private final IBenefitRepository benefitRepository;
    private final CredentialsRepository credentialsRepository;
    private final CafeRepository cafeRepository;

    @Override
    @Transactional
    public RedemptionResponse create(RedemptionCreateRequest redemptionCreateRequest) {
        ClientEntity client = clientRepository.findByExternalId(redemptionCreateRequest.client())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        BenefitEntity benefit = benefitRepository.findByExternalId(redemptionCreateRequest.benefit())
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado"));

        if (!benefit.getStatus() || !benefit.getIsPublic()) {
            throw new IllegalStateException("El beneficio no está habilitado");
        }
        if (benefit.getEndDate() != null && benefit.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("El beneficio está vencido");
        }
        if (benefit.getMaxRedemptions() <= 0) {
            throw new IllegalStateException("El beneficio no tiene canjes disponibles");
        }
        if (client.getCurrentPoints() < benefit.getPointsCost()) {
            throw new IllegalStateException("El cliente no tiene suficientes puntos");
        }

        client.setCurrentPoints(client.getCurrentPoints() - benefit.getPointsCost());
        clientRepository.save(client);

        benefit.setMaxRedemptions(benefit.getMaxRedemptions() - 1);
        benefitRepository.save(benefit);

        RedemptionEntity redemption = createMapper.toEntity(redemptionCreateRequest);
        redemption.setExternalId(UUID.randomUUID());
        redemption.setClient(client);
        redemption.setBenefit(benefit);
        redemption.setState(true);
        redemption.setDate(LocalDateTime.now());
        redemption.setValidationCode(UUID.randomUUID().toString());
        redemption.setUsedPoints(benefit.getPointsCost());
        redemptionRepository.save(redemption);
        return responseMapper.toDto(redemption);
    }

    @Override
    @Transactional
    public void deleteByid(UUID externalId) {

        RedemptionEntity redemptionEntity = redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));
        redemptionRepository.delete(redemptionEntity);
    }

    @Override
    public List<RedemptionResponse> findbyClients(UUID externalId) {
        ClientEntity client = clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("cliente no encontrado"));
        return redemptionRepository.findByClientAndState(client, false)
                .stream().map(responseMapper::toDto).toList();
    }

    @Override
    @Transactional
    public RedemptionResponse validate(String validationCode) {
        RedemptionEntity redemption = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(()-> new EntityNotFoundException("Redemption not found"));


        if (!redemption.getState()) {
            throw new IllegalStateException("Este código ya fue utilizado");
        }

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));

        if (!redemption.getBenefit().getCafeEntity().getExternalId()
                .equals(cafe.getExternalId())) {
            throw new IllegalArgumentException("Este canje no pertenece a tu cafetería");
        }

        redemption.setState(false);
        redemptionRepository.save(redemption);
        return responseMapper.toDto(redemption);
    }

    @Override
    @Transactional
    public RedemptionResponse cancel(String validationCode) {
        RedemptionEntity redemption = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new EntityNotFoundException("Código no válido"));

        if (!redemption.getState()) {
            throw new IllegalStateException("Este código ya fue utilizado o cancelado");
        }

        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));

        if (!redemption.getBenefit().getCafeEntity().getExternalId()
                .equals(cafe.getExternalId())) {
            throw new IllegalArgumentException("Este canje no pertenece a tu cafetería");
        }

        ClientEntity client = redemption.getClient();
        client.setCurrentPoints(client.getCurrentPoints() + redemption.getUsedPoints());
        clientRepository.save(client);

        BenefitEntity benefit = redemption.getBenefit();
        benefit.setMaxRedemptions(benefit.getMaxRedemptions() + 1);
        benefitRepository.save(benefit);

        redemptionRepository.delete(redemption);
        return responseMapper.toDto(redemption);
    }
}
