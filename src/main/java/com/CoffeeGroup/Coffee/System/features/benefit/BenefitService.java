package com.CoffeeGroup.Coffee.System.features.benefit;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BenefitService implements IBenefitService {

    private final IBenefitRepository benefitRepository;
    private final IBenefitTypeRepository benefitTypeRepository;
    private final CafeRepository cafeRepository;
    private final TierRepository tierRepository;
    private final IBenefitCreateMapper createMapper;
    private final IBenefitResponseMapper responseMapper;

    @Transactional
    @Override
    public BenefitResponse save(BenefitCreateRequest benefitCreate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria && benefitCreate.cafeId() == null) {
            throw new IllegalArgumentException("Una cafetería no puede crear un beneficio global");
        }

        TierEntity tierEntity = tierRepository.findByExternalId(benefitCreate.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier no encontrado"));
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(benefitCreate.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de beneficio no encontrado"));
        CafeEntity cafeEntity = null;
        if (benefitCreate.cafeId() != null) {
            cafeEntity = cafeRepository.findByExternalId(benefitCreate.cafeId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe no encontrado"));
        }

        BenefitEntity benefit = createMapper.toEntity(benefitCreate);
        benefit.setExternalId(UUID.randomUUID());
        benefit.setBenefitType(benefitType);
        benefit.setTierEntity(tierEntity);
        benefit.setCafeEntity(cafeEntity);
        benefit.setIsPublic(false);
        benefit.setIsGlobal(cafeEntity == null
                ? GlobalBenefit.GLOBAL_BENEFIT
                : GlobalBenefit.CAFFETERIA_BENEFIT);

        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    @Override
    public void delete(UUID externalId) {
        BenefitEntity benefit = findBenefit(externalId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));
        if (isCafeteria && benefit.getIsGlobal().equals(GlobalBenefit.GLOBAL_BENEFIT)) {
            throw new IllegalArgumentException("Una cafetería no puede elimnar un beneficio global");
        }
        benefitRepository.delete(benefit);
    }

    @Override
    public List<BenefitResponse> findAll() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"));
        if (isAdmin){
            return benefitRepository.findByIsGlobal(GlobalBenefit.GLOBAL_BENEFIT).stream()
                    .map(responseMapper::toDto).toList();
        }
        return benefitRepository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public BenefitResponse findByBenefit(UUID externalId){
        BenefitEntity benefit = findBenefit(externalId);
        return responseMapper.toDto(benefit);
    }

    @Transactional
    public BenefitResponse confirmBenefit(UUID externalId){
        return setVisibility(externalId,false);
    }

    @Transactional
    public BenefitResponse cancelBenefit(UUID externalId){
        return setVisibility(externalId,false);
    }

    @Transactional
    public BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest newEndDateRequest){
        BenefitEntity benefit = findBenefit(externalId);
        benefit.setEndDate(newEndDateRequest.endDate());
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    public BenefitEntity findBenefit(UUID externalId){
        BenefitEntity benefitEntity = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        return benefitEntity;
    }

     private BenefitResponse setVisibility(UUID externalId, boolean isPublic){
        BenefitEntity benefit = findBenefit(externalId);
        benefit.setIsPublic(isPublic);
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
     }
}