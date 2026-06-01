package com.CoffeeGroup.Coffee.System.features.shared_rewards;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientService;
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
    private final IClientRepository clientRepository;
    private final IBenefitRepository benefitRepository;

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
        ClientEntity receiver = clientRepository.findByExternalId(request.receiver())
                .orElseThrow(()-> new EntityNotFoundException("Cliente not found"));
        ClientEntity transmitter = clientRepository.findByExternalId(request.transmitter())
                .orElseThrow(()-> new EntityNotFoundException("Cliente not found"));
        BenefitEntity benefit = benefitRepository.findByExternalId(request.benefit())
                        .orElseThrow(() -> new EntityNotFoundException("Benefit not found"));
        entity.setExternalId(UUID.randomUUID());
        entity.setDate(LocalDate.now());
        entity.setReceiver(receiver);
        entity.setTransmitter(transmitter);
        entity.setBenefit(benefit);
        return mapper.toDto(repository.save(entity));
    }

    public void delete(UUID sharedRewardId){
        repository.delete(
                repository.findByExternalId(sharedRewardId)
                        .orElseThrow(() -> new NoSuchElementException("Beneficio compartido no encontrado"))
        );
    }
}
