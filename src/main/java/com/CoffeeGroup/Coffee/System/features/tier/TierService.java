package com.CoffeeGroup.Coffee.System.features.tier;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.NewTierMapper;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.TierResponseMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TierService implements ITierService {

    private final TierRepository tierRepository;
    private final NewTierMapper newTierMapper;
    private final TierResponseMapper tierMapper;

    @Override
    public TierResponse create(CreateTierRequest request) {
        String name = request.name().trim();
        validateTierNameDoesNotExist(name);
        validateTierPointsDoesNotExist(request.minPoints());

        TierEntity tierEntity = newTierMapper.toEntity(new CreateTierRequest(name, request.minPoints()));
        tierEntity.setExternalId(UUID.randomUUID());
        tierRepository.save(tierEntity);
        return tierMapper.toDto(tierEntity);
    }

    @Override
    public List<TierResponse> getAll() {
        return tierRepository.findAllByOrderByMinPointsAsc()
                .stream()
                .map(tierMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TierResponse getById(UUID externalId) {

        return tierRepository.findByExternalId(externalId)
                .map(tierMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
    }

    @Override
    public TierResponse update(UUID externalId, CreateTierRequest request) {
        TierEntity tier = tierRepository.findByExternalId(externalId).orElseThrow(() -> new EntityNotFoundException("Tier not found"));
        String name = request.name().trim();
        validateBaseTierKeepsZeroPoints(tier, request.minPoints());
        validateTierNameDoesNotExistInAnother(name, externalId);
        validateTierPointsDoesNotExistInAnother(request.minPoints(), externalId);

        tier.setName(name);
        tier.setMinPoints(request.minPoints());
        return tierMapper.toDto(tierRepository.save(tier));
    }

    @Override
    public void delete(UUID externalId) {
        TierEntity tier = tierRepository.findByExternalId(externalId)
                        .orElseThrow(() -> new EntityNotFoundException("tier not found"));

        tierRepository.delete(tier);
    }


    private void validateTierNameDoesNotExist(String name) {
        if (tierRepository.existsByNameIgnoreCase(name)) {
            throw new EntityAlreadyExistsException("A tier named " + name + " already exists");
        }
    }

    private void validateTierPointsDoesNotExist(Integer minPoints) {
        if (tierRepository.existsByMinPoints(minPoints)) {
            throw new EntityAlreadyExistsException("A tier with " + minPoints + " points already exists");
        }
    }

    private void validateTierNameDoesNotExistInAnother(String name, UUID externalId) {
        if (tierRepository.existsByNameIgnoreCaseAndExternalIdNot(name, externalId)) {
            throw new EntityAlreadyExistsException("A tier named " + name + " already exists");
        }
    }

    private void validateTierPointsDoesNotExistInAnother(Integer minPoints, UUID externalId) {
        if (tierRepository.existsByMinPointsAndExternalIdNot(minPoints, externalId)) {
            throw new EntityAlreadyExistsException("A tier with " + minPoints + " points already exists");
        }
    }

    // Client registration needs a tier with 0 points, so the base tier cannot move off it.
    private void validateBaseTierKeepsZeroPoints(TierEntity tier, Integer requestedMinPoints) {
        if (tier.getMinPoints() != null && tier.getMinPoints() == 0 && requestedMinPoints != 0) {
            throw new IllegalStateException("The base tier must keep 0 points");
        }
    }
}
