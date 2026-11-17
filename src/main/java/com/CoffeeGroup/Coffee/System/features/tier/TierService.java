package com.CoffeeGroup.Coffee.System.features.tier;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.TierMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TierService implements ITierService {

    private final TierRepository tierRepository;
    private final TierMapper tierMapper;

    @Override
    @Transactional
    public TierResponse create(CreateTierRequest request) {
        String name = request.name().trim();
        if (tierRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException("Ya existe un tier llamado " + name);
        }
        if (tierRepository.existsByMinPoints(request.minPoints())) {
            throw new EntityAlreadyExistsException("Ya existe un tier con " + request.minPoints() + " puntos");
        }
        TierEntity tier = TierEntity.builder().name(name).minPoints(request.minPoints()).build();
        tierRepository.save(tier);
        return tierMapper.toResponse(tier);
    }

    @Override
    public List<TierResponse> getAll() {
        return tierRepository.findAllByOrderByMinPointsAsc().stream().map(tierMapper::toResponse).toList();
    }

    @Override
    public TierResponse getById(UUID externalId) {
        return tierMapper.toResponse(findTier(externalId));
    }

    @Override
    @Transactional
    public TierResponse update(UUID externalId, CreateTierRequest request) {
        TierEntity tier = findTier(externalId);
        String name = request.name().trim();
        if (tier.isBase() && request.minPoints() != 0) {
            throw new BusinessRuleException("El tier base debe mantener 0 puntos");
        }
        if (tierRepository.existsByNameIgnoreCaseAndExternalIdNot(name, externalId)) {
            throw new EntityAlreadyExistsException("Ya existe un tier llamado " + name);
        }
        if (tierRepository.existsByMinPointsAndExternalIdNot(request.minPoints(), externalId)) {
            throw new EntityAlreadyExistsException("Ya existe un tier con " + request.minPoints() + " puntos");
        }
        tier.setName(name);
        tier.setMinPoints(request.minPoints());
        return tierMapper.toResponse(tier);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        tierRepository.delete(findTier(externalId));
        tierRepository.flush();
    }

    private TierEntity findTier(UUID externalId) {
        return tierRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Tier no encontrado"));
    }
}
