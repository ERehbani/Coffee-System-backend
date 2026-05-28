package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreate;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.BenefitMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BenefitService implements IBenefitService {

    private final IBenefitRepository benefitRepository;
    private final IBenefitTypeRepository benefitTypeRepository;
    private final ICafeRepository cafeRepository;
    private final TierRepository tierRepository;
    private final IClientRepository clientRepository;
    private final BenefitMapper benefitMapper;

    @Override
    @Transactional
    public BenefitResponse save(BenefitCreate dto) {

        BenefitTypeEntity type = benefitTypeRepository.findByExternalId(dto.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de beneficio no encontrado con ID: " + dto.typeId()));


        CafeEntity cafe = null;
        if (dto.cafeId() != null) {
            cafe = cafeRepository.findByExternalId(dto.cafeId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada con ID: " + dto.cafeId()));
        }


        TierEntity tier = tierRepository.findByExternalId(dto.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier no encontrado con ID: " + dto.tierId()));


        if (dto.pointsCost() == null) {
            throw new IllegalArgumentException("El costo de puntos no puede ser nulo");
        }

        BenefitEntity entity = benefitMapper.toEntity(dto, type, cafe, tier);
        BenefitEntity saved = benefitRepository.save(entity);

        return benefitMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        BenefitEntity entity = benefitRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado con ID: " + externalId));
        benefitRepository.delete(entity);
    }

    @Override
    public List<BenefitResponse> all() {
        return benefitRepository.findAll()
                .stream()
                .map(benefitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BenefitResponse> shareByPointCost(Integer pointCost) {
        return benefitRepository.findAll()
                .stream()
                .filter(b -> b.getPointsCost() != null && b.getPointsCost().equals(pointCost))
                .map(benefitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BenefitResponse> shareByEndDate(Date date) {
        return benefitRepository.findAll()
                .stream()
                .filter(b -> b.getEndDate() != null && b.getEndDate().equals(date))
                .map(benefitMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BenefitResponse> shareByStatus(Boolean status) {
        return benefitRepository.findAll()
                .stream()
                .filter(b -> b.getStatus() != null && b.getStatus().equals(status))
                .map(benefitMapper::toDto)
                .collect(Collectors.toList());
    }

    public BenefitResponse findById(Long id) {
        return benefitRepository.findById(id)
                .map(benefitMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado con ID: " + id));
    }

    public BenefitResponse findByExternalId(UUID externalId) {
        return benefitRepository.findByExternalId(externalId)
                .map(benefitMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado con ID: " + externalId));
    }

    public BenefitEntity findEntityByExternalId(UUID externalId) {
        return benefitRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado con ID: " + externalId));
    }

    @Transactional
    public BenefitResponse update(UUID externalId, BenefitCreate dto) {
        BenefitEntity existing = findEntityByExternalId(externalId);

        BenefitTypeEntity type = benefitTypeRepository.findByExternalId(dto.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de beneficio no encontrado"));

        CafeEntity cafe = null;
        if (dto.cafeId() != null) {
            cafe = cafeRepository.findByExternalId(dto.cafeId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));
        }

        TierEntity tier = tierRepository.findByExternalId(dto.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier no encontrado"));

        existing.setDescription(dto.description());
        existing.setPrice(dto.price());
        existing.setPointsCost(dto.pointsCost());
        existing.setStartDate(dto.startDate());
        existing.setEndDate(dto.endDate());
        existing.setIsPublic(dto.isPublic());
        existing.setMaxRedemptions(dto.maxRedemptions());
        existing.setType(type);
        existing.setCafe(cafe);
        existing.setTier(tier);

        BenefitEntity saved = benefitRepository.save(existing);
        return benefitMapper.toDto(saved);
    }

 //Capaz see rompe pero verifica si un cliente puede canjear un beneficio, true si puede canjear, false si no

    public boolean canRedeemBenefit(UUID clientExternalId, UUID benefitExternalId) {
        ClientEntity client = clientRepository.findByExternalId(clientExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado con ID: " + clientExternalId));

        BenefitEntity benefit = benefitRepository.findByExternalId(benefitExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado con ID: " + benefitExternalId));

        return canRedeemBenefit(client, benefit);
    }

    private boolean canRedeemBenefit(ClientEntity client, BenefitEntity benefit) {

        if (!Boolean.TRUE.equals(benefit.getStatus())) {
            throw new IllegalStateException("El beneficio no está activo");
        }

        if (benefit.getEndDate() != null && benefit.getEndDate().before(new Date())) {
            throw new IllegalStateException("El beneficio ha vencido");
        }

        TierEntity clientTier = client.getTier();
        TierEntity benefitTier = benefit.getTier();

        if (clientTier.getMinPoints() < benefitTier.getMinPoints()) {
            throw new IllegalArgumentException(
                    "Tu tier (" + clientTier.getName() + ") no es suficiente. Necesitas tier " + benefitTier.getName() + " o superior"
            );
        }

        if (client.getCurrentPoints() < benefit.getPointsCost()) {
            throw new IllegalArgumentException(
                    "No tienes puntos suficientes. Tienes: " + client.getCurrentPoints() +
                            ", Necesitas: " + benefit.getPointsCost()
            );
        }

        return true;
    }

    @Transactional
    public BenefitResponse redeemBenefit(UUID clientExternalId, UUID benefitExternalId) {
        ClientEntity client = clientRepository.findByExternalId(clientExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));

        BenefitEntity benefit = benefitRepository.findByExternalId(benefitExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Beneficio no encontrado"));

        canRedeemBenefit(client, benefit);

        client.setCurrentPoints(client.getCurrentPoints() - benefit.getPointsCost());
        client.setHistoricalPoints(client.getHistoricalPoints() + benefit.getPointsCost());
        clientRepository.save(client);

        return benefitMapper.toDto(benefit);
    }
}