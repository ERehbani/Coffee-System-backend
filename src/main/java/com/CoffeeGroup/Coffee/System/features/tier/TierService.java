package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierResponseDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers.NewTierMapper;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers.TierResponseMapper;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TierResponseDTO create(NewTierDTO request) {
        validateTierNameDoesNotExist(request.name());
        validateTierPointsDoesNotExist(request.minPoints());

        TierEntity tierEntity = newTierMapper.toEntity(request);
        tierEntity.setExternalId(UUID.randomUUID());
        tierRepository.save(tierEntity);
        return tierMapper.toDto(tierEntity);
    }

    @Override
    public List<TierResponseDTO> getAll() {
        return tierRepository.findAll()
                .stream()
                .map(tierMapper::toDto)
                .collect(Collectors.toList());
    }
    /// //
    @Override
    public TierResponseDTO getById(UUID externalId) {
        TierEntity tier = tierRepository.findByExternalId(externalId)
                .orElseThrow(() -> new  EntityNotFoundException("Tier not found"));
        return tierMapper.toDto(tier);
    }

    @Override
    public TierResponseDTO update(UUID externalId, NewTierDTO request) {
        TierEntity tier = findByExternalIdOrThrow(externalId);
        tier.setName(request.name());
        tier.setMinPoints(request.minPoints());
        return tierMapper.toDto(tierRepository.save(tier));
    }

    @Override
    public void delete(UUID externalId) {
        TierEntity tier = tierRepository.findByExternalId(externalId)
                        .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
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