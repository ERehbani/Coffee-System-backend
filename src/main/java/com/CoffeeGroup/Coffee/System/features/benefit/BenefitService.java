package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BenefitService implements IBenefitService {

    private final IBenefitRepository benefitRepository;
    private final IBenefitTypeRepository benefitTypeRepository;
    private final ICafeRepository cafeRepository;
    private final TierRepository tierRepository;
    private final IBenefitCreateMapper createMapper;
    private final IBenefitResponseMapper responseMapper;

    @Transactional
    @Override
    public BenefitResponse save(BenefitCreateRequest benefitCreate) {
        CafeEntity cafeEntity = cafeRepository.findByExternalId(benefitCreate.cafeId())
                .orElseThrow(() -> new EntityNotFoundException("cafe not found"));
        TierEntity tierEntity = tierRepository.findByExternalId(benefitCreate.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(benefitCreate.typeId())
                .orElseThrow(()-> new EntityNotFoundException("Type Benefit not found"));

        BenefitEntity benefit = createMapper.toEntity(benefitCreate);
        benefit.setExternalId(UUID.randomUUID());
        benefit.setBenefitType(benefitType);
        benefit.setTierEntity(tierEntity);
        benefit.setCafeEntity(cafeEntity);
        benefit.setIsPublic(false);
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }
    @Override
    public void delete(UUID externalId) {
        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        benefitRepository.delete(benefit);
    }

    @Override
    public List<BenefitResponse> findAll() {
        return benefitRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public BenefitResponse findByBenefit(UUID externalId){
        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        return responseMapper.toDto(benefit);
    }

    public BenefitResponse confirmBenefit(UUID externalId){
        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        benefit.setIsPublic(true);
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }


    public BenefitResponse cancelBenefit(UUID externalId){
        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        benefit.setIsPublic(false);
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    @Transactional
    public BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest newEndDateRequest){
        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        benefit.setEndDate(newEndDateRequest.endDate());
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

//    public BenefitEntity findBenefit(UUID externalId){
//        BenefitEntity benefit = benefitRepository.findByExternalId(externalId)
//                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
//        return benefit;
//    }

}