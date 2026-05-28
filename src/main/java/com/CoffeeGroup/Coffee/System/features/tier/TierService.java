package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.CreateTierRequest;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponse;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.NewTierMapper;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mapper.TierResponseMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TierService implements ITierService {

    private final TierRepository tierRepository;
    private final NewTierMapper newTierMapper;
    private final TierResponseMapper tierMapper;

    @Override
    public TierResponse create(CreateTierRequest request) {
        validateTierNameDoesNotExist(request.name());
        validateTierPointsDoesNotExist(request.minPoints());

        TierEntity tierEntity = newTierMapper.toEntity(request);
        tierEntity.setExternalId(UUID.randomUUID());
        tierRepository.save(tierEntity);
        return tierMapper.toDto(tierEntity);
    }

    @Override
    public List<TierResponse> getAll() {
        return tierRepository.findAll()
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
        TierEntity tier = findByExternalIdOrThrow(externalId);
        tier.setName(request.name());
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
        if (tierRepository.existsByName(name)) {
            throw new EntityAlreadyExistsException("Tier " + name + " already exists");
        }
    }

    private void validateTierPointsDoesNotExist(Integer minPoints) {
        if (tierRepository.existsByMinPoints(minPoints)) {
            throw new EntityAlreadyExistsException("Tier already with" + minPoints + " min points");
        }
    }

    private TierEntity findByExternalIdOrThrow(UUID externalId) {
        return tierRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
    }


}