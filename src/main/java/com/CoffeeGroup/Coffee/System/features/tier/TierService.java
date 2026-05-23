package com.CoffeeGroup.Coffee.System.features.tier;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.NewTierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.dto.TierDTO;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers.NewTierMapper;
import com.CoffeeGroup.Coffee.System.features.tier.domain.mappeers.TierMapper;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class TierService implements ITierService {

    private final TierRepository tierRepository;
    private final NewTierMapper newTierMapper;
    private final TierMapper tierMapper;

    @Override
    @Transactional
    public TierDTO create(NewTierDTO request, UserEntity user) {
        validateSuperAdmin(user);
        validateTierDoesNotExist(request.nombre());

        TierEntity tierEntity = newTierMapper.toEntity(request);
        TierEntity saved = tierRepository.save(tierEntity);
        return tierMapper.toDto(saved);
    }

    @Override
    public List<TierDTO> findAll() {
        return tierRepository.findAll()
                .stream()
                .map(tierMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public TierDTO getById(UUID externalId) {
        TierEntity tier = findByExternalIdOrThrow(externalId);
        return tierMapper.toDto(tier);
    }

    @Override
    @Transactional
    public TierDTO update(UUID externalId, NewTierDTO request, UserEntity user) {
        validateSuperAdmin(user);

        TierEntity tier = findByExternalIdOrThrow(externalId);
        validateTierDoesNotExist(request.nombre(), tier.getId());

        tier.setName(request.nombre());
        tier.setMinPoints(request.nombre().getMinimumPoints());

        TierEntity saved = tierRepository.save(tier);
        return tierMapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID externalId, UserEntity user) {
        validateSuperAdmin(user);
        TierEntity tier = findByExternalIdOrThrow(externalId);
        tierRepository.delete(tier);
    }

    private void validateSuperAdmin(UserEntity user) {
        if (!"SUPER_ADMIN".equals(user.getRole().getName())) {
            throw new SecurityException("Solo Super Admin puede realizar esta acción");
        }
    }

    private void validateTierDoesNotExist(ETiersEnum name) {
        if (tierRepository.findAll().stream()
                .anyMatch(t -> t.getName() == name)) {
            throw new EntityAlreadyExistsException("El tier " + name + " ya existe");
        }
    }

    private void validateTierDoesNotExist(ETiersEnum name, Long currentId) {
        if (tierRepository.findAll().stream()
                .filter(t -> !t.getId().equals(currentId))
                .anyMatch(t -> t.getName() == name)) {
            throw new EntityAlreadyExistsException("El tier " + name + " ya existe");
        }
    }

    private TierEntity findByExternalIdOrThrow(UUID externalId) {
        return tierRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Tier no encontrado"));
    }
}