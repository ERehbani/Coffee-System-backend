package com.CoffeeGroup.Coffee.System.features.redemption;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.IBenefitRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.IPurchaseRepository;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionCreateMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers.IRedemptionResponseMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionCreateRequest;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.Mappers.IReviewCreateRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RedemptionService implements IRedemptionService {

    private final IRedemptionRepository redemptionRepository;
    private final IRedemptionResponseMapper responseMapper;
    private final IRedemptionCreateMapper createMapper;
    private final IClientRepository clientRepository;
    private final IPurchaseRepository purchaseRepository;
    private final IBenefitRepository benefitRepository;

    @Override
    @Transactional
    public RedemptionResponse create(RedemptionCreateRequest redemptionCreateRequest) {
        ClientEntity clientAux = clientRepository.findByExternalId(redemptionCreateRequest.client()).
                orElseThrow(()-> new EntityNotFoundException("Client not found"));


        BenefitEntity benefitAux = benefitRepository.findByExternalId(redemptionCreateRequest.benefit())
                .orElseThrow(() -> new EntityNotFoundException("Benefit not found"));
        PurchaseEntity purchaseAux = null;


        if (redemptionCreateRequest.purchase() != null){
            purchaseAux = purchaseRepository.findByExternalId(redemptionCreateRequest.purchase())
                    .orElseThrow(()-> new EntityNotFoundException("Purchase not found"));

        }

        if (benefitAux.getStatus() == false && benefitAux.getIsPublic() == false){
            throw new IllegalStateException("el beneficio no esta habilitado");
        }

        if (benefitAux.getMaxRedemptions() <= 0){
            throw new IllegalStateException("el beneficio se quedo sin tokens// modificar a ingles");
        }
        benefitAux.setMaxRedemptions(benefitAux.getMaxRedemptions() - 1);
        benefitRepository.save(benefitAux);

        Integer pointsCost = benefitAux.getPointsCost();
        if (clientAux.getCurrentPoints() < pointsCost) {
            throw new IllegalStateException("The customer does not have enough points");
        }
        clientAux.setCurrentPoints(clientAux.getCurrentPoints() - pointsCost);
        clientRepository.save(clientAux);

        RedemptionEntity redemptionEntity =  createMapper.toEntity(redemptionCreateRequest);

        redemptionEntity.setClient(clientAux);
        redemptionEntity.setBenefit(benefitAux);
        redemptionEntity.setPurchase(purchaseAux);
        redemptionEntity.setState(true);
        redemptionEntity.setDate(LocalDateTime.from(LocalDate.now()));
        redemptionEntity.setValidationCode(String.valueOf(UUID.randomUUID()));
        redemptionEntity.setUsedPoints(pointsCost);

        redemptionRepository.save(redemptionEntity);
        return responseMapper.toDto(redemptionEntity);
    }

    @Override
    @Transactional
    public RedemptionResponse update(UUID externalId, RedemptionUpdateRequest redemptionUpdateRequest) {
        RedemptionEntity save = redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));
        save.setState(redemptionUpdateRequest.state());
        save.setExternalId(redemptionUpdateRequest.externalId());

        redemptionRepository.save(save);
        return responseMapper.toDto(save);
    }

    @Override
    public RedemptionResponse getById(UUID externalId) {
        return redemptionRepository.findByExternalId(externalId).map(responseMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));
    }

    @Override
    @Transactional
    public void deleteByid(UUID externalId) {
        RedemptionEntity redemptionEntity = redemptionRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Redemption not found"));

        redemptionRepository.delete(redemptionEntity);
    }

    @Override
    public List<RedemptionResponse> findAll() {
        return redemptionRepository.findAll().stream().map(responseMapper::toDto).toList();
    }
}
