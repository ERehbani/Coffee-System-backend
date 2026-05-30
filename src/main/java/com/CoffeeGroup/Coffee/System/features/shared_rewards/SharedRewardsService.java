package com.CoffeeGroup.Coffee.System.features.shared_rewards;

import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsRequest;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsResponse;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.mapper.ISharedRewardsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharedRewardsService implements ISharedRewardsService{

    private final ISharedRewardsRepository repository;
    private final ISharedRewardsMapper mapper;

    public List<SharedRewardsResponse> findAll(){
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    public SharedRewardsResponse findById(UUID sharedRewardId){
        SharedRewardsEntity entity = repository.findByExternalId(sharedRewardId)
                .orElseThrow(() -> new NoSuchElementException("Beneficio compartido no encontrado"));
        return mapper.toDto(entity);
    }

    public SharedRewardsResponse save(SharedRewardsRequest request){
        SharedRewardsEntity entity = mapper.toEntity(request);

        entity.setExternalId(UUID.randomUUID());
        entity.setDate(LocalDate.now());

        return mapper.toDto(repository.save(entity));
    }

    public void delete(UUID sharedRewardId){
        repository.delete(
                repository.findByExternalId(sharedRewardId)
                        .orElseThrow(() -> new NoSuchElementException("Beneficio compartido no encontrado"))
        );
    }
}
