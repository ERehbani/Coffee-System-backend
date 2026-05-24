package com.CoffeeGroup.Coffee.System.features.purchase;


import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.CreatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.PurchaseResponse;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.dto.UpdatePurchaseRequest;
import com.CoffeeGroup.Coffee.System.features.purchase.domain.mapper.PurchaseMapper;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PurchaseService implements IPurchaseService{

    private final IPurchaseRepository repository;
    private final PurchaseMapper mapper;

    private final IClientRepository clientRepository;
    private final ICafeRepository cafeRepository;


    @Override
    public PurchaseResponse create(CreatePurchaseRequest request) {

        ClientEntity client = clientRepository.findByExternalId(request.client())
                .orElseThrow(() -> new EntityNotFoundException("The client does not exists in the repository"));

        CafeEntity cafe = cafeRepository.findByExternalId(request.externalId())
                .orElseThrow(() -> new EntityNotFoundException("The cafe does not exists in the repository"));

        PurchaseEntity entity = mapper.toEntity(request,client,cafe);

        return mapper.toResponse(repository.save(entity));

    }

    @Override
    public List<PurchaseResponse> findAll() {

        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public PurchaseResponse getById(UUID externalId) {
        PurchaseEntity entity = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("The purchase does not exists in the repository"));

        return mapper.toResponse(entity);
    }

    @Override
    public PurchaseResponse update(UUID externalId, UpdatePurchaseRequest request) {

        PurchaseEntity entity = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("The purchase does not exist in the repository"));

        if (request.date() != null) {

            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date fechaTransformada = formatter.parse(request.date());
                entity.setDate(fechaTransformada);

            } catch (java.text.ParseException ex) {
                throw new IllegalArgumentException("The date format is incorrect. It should be yyyy-MM-dd" , ex);
            }
        }

        if (request.price() != null) {
            entity.setPrice(request.price());
        }


        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void delete(UUID externalId) {

        PurchaseEntity entity = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("The purchase does not exists in the repository"));

        repository.delete(entity);
    }
}
