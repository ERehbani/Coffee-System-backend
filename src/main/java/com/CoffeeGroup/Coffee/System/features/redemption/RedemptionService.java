package com.CoffeeGroup.Coffee.System.features.redemption;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
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
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public RedemptionResponse create(RedemptionCreateRequest redemptionCreateRequest) {
        ClientEntity client = clientRepository.findByExternalId(redemptionCreateRequest.client())
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));

        BenefitEntity benefit = benefitRepository.findByExternalId(redemptionCreateRequest.benefit())
                .orElseThrow(() -> new EntityNotFoundException("Benefit not found"));

        CafeEntity cafe = benefit.getCafeEntity();

        if (!benefit.getStatus() || !benefit.getIsPublic()) {
            throw new IllegalStateException("Benefit is not enabled");
        }
        if (benefit.getEndDate() != null && benefit.getEndDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Benefit has expired");
        }
        if (benefit.getMaxRedemptions() <= 0) {
            throw new IllegalStateException("Benefit has no available redemptions");
        }
        if (client.getCurrentPoints() < benefit.getPointsCost()) {
            throw new IllegalStateException("Client does not have enough points");
        }

        client.setCurrentPoints(client.getCurrentPoints() - benefit.getPointsCost());
        clientRepository.save(client);

        benefit.setMaxRedemptions(benefit.getMaxRedemptions() - 1);
        benefitRepository.save(benefit);

        RedemptionEntity redemption = createMapper.toEntity(redemptionCreateRequest);
        redemption.setExternalId(UUID.randomUUID());
        redemption.setClient(client);
        redemption.setBenefit(benefit);
        redemption.setCafeEntity(cafe);
        redemption.setState(true);
        redemption.setDate(LocalDateTime.now());
        redemption.setValidationCode(UUID.randomUUID().toString());
        redemption.setUsedPoints(benefit.getPointsCost());
        redemptionRepository.save(redemption);
        return responseMapper.toDto(redemption);
    }

    @Override
    @Transactional
    public void deleteById(UUID externalId) {

        RedemptionEntity redemptionEntity = redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));
        redemptionRepository.delete(redemptionEntity);
    }

    @Override
    public List<RedemptionResponse> findAll(UUID externalId) {
        ClientEntity client = clientRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found"));
        return redemptionRepository.findByClientAndState(client, true)
                .stream().map(responseMapper::toDto).toList();
    }

    @Override
    @Transactional
    public RedemptionResponse validate(String validationCode) {
        RedemptionEntity redemption = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));

        if (!redemption.getState()) {
            throw new IllegalStateException("This code has already been used");
        }

        if (redemption.getBenefit().getCafeEntity() == null) {
            if (!securityUtils.isAdmin()) {
                throw new IllegalArgumentException("Only admin can validate global benefits");
            }
        } else {
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!redemption.getBenefit().getCafeEntity().getExternalId()
                    .equals(cafe.getExternalId())) {
                throw new IllegalArgumentException("This redemption does not belong to your cafe");
            }
        }

        redemption.setState(false);
        redemptionRepository.save(redemption);
        return responseMapper.toDto(redemption);
    }

    @Override
    @Transactional
    public RedemptionResponse cancel(String validationCode) {
        RedemptionEntity redemption = redemptionRepository.findByValidationCode(validationCode)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));

        if (!redemption.getState()) {
            throw new IllegalStateException("This code has already been used");
        }

        if (redemption.getBenefit().getCafeEntity() == null) {
            if (!securityUtils.isAdmin()) {
                throw new IllegalArgumentException("Only admin can cancel global benefits");
            }
        } else {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            CredentialsEntity credentials = credentialsRepository.findByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!redemption.getBenefit().getCafeEntity().getExternalId().equals(cafe.getExternalId())) {
                throw new IllegalArgumentException("This redemption does not belong to your cafe");
            }
        }
        ClientEntity client = redemption.getClient();
        client.setCurrentPoints(client.getCurrentPoints() + redemption.getUsedPoints());
        clientRepository.save(client);

        BenefitEntity benefit = redemption.getBenefit();
        benefit.setMaxRedemptions(benefit.getMaxRedemptions() + 1);
        benefitRepository.save(benefit);

        redemption.setState(false);
        redemptionRepository.save(redemption);
        return responseMapper.toDto(redemption);
    }
}
