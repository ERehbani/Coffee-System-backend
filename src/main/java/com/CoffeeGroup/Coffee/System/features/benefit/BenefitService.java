package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BenefitService implements IBenefitService {

    private final IBenefitRepository benefitRepository;
    private final IBenefitTypeRepository benefitTypeRepository;
    private final CafeRepository cafeRepository;
    private final TierRepository tierRepository;
    private final IBenefitCreateMapper createMapper;
    private final IBenefitResponseMapper responseMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    @Override
    public BenefitResponse save(BenefitCreateRequest benefitCreate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria && benefitCreate.cafeId() == null) {
            throw new IllegalArgumentException("A cafe cannot create a global benefit");
        }

        TierEntity tierEntity = tierRepository.findByExternalId(benefitCreate.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(benefitCreate.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));
        CafeEntity cafeEntity = null;
        if (benefitCreate.cafeId() != null) {
            cafeEntity = cafeRepository.findByExternalId(benefitCreate.cafeId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
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
        BenefitEntity benefit = getBenefit(externalId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));
        if (isCafeteria && benefit.getIsGlobal().equals(GlobalBenefit.GLOBAL_BENEFIT)) {
            throw new IllegalArgumentException("A cafe cannot delete a global benefit");
        }
        benefitRepository.delete(benefit);
    }

    @Override
    public List<BenefitResponse> getAll() {
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
    public BenefitResponse getByBenefit(UUID externalId){
        BenefitEntity benefit = getBenefit(externalId);
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
        BenefitEntity benefit = getBenefit(externalId);
        benefit.setEndDate(newEndDateRequest.endDate());
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    public BenefitEntity getBenefit(UUID externalId){
        BenefitEntity benefitEntity = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        return benefitEntity;
    }

     private BenefitResponse setVisibility(UUID externalId, boolean isPublic){
        BenefitEntity benefit = getBenefit(externalId);
        benefit.setIsPublic(isPublic);
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
     }


    @Override
    public List<BenefitResponse> getAllByCafe(UUID cafeExternalId) {

        CafeEntity cafe;

        if(securityUtils.isAdmin()){
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view the benefits of this coffee shop"));
        }

        return benefitRepository.findByCafeEntity(cafe).stream()
                .map(responseMapper::toDto)
                .toList();
    }

    @Override
    public BenefitResponse getBenefitByCafe(UUID cafeExternalId, UUID benefitExternalId) {

        CafeEntity cafe;

        if(securityUtils.isAdmin()){
            cafe = cafeRepository.findByExternalId(cafeExternalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeExternalId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view the benefits of this coffee shop"));
        }

        BenefitEntity benefit = benefitRepository.findByCafeEntityAndExternalId(cafe,benefitExternalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit not found"));


        return responseMapper.toDto(benefit);
    }

    public List<BenefitResponse> filterBenefitsByCafe(UUID cafeId, String name, Boolean status, Boolean isPublic, BenefitType type) {
        CafeEntity cafe;

        if (securityUtils.isAdmin()) {
            cafe = cafeRepository.findByExternalId(cafeId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to filter benefits of this cafe"));
        }

        List<BenefitEntity> filteredBenefits;

        if (type != null) {
            filteredBenefits = benefitRepository.findByCafeEntityAndBenefitType_Name(cafe, type);
        } else if (name != null && status != null && isPublic != null) {
            filteredBenefits = benefitRepository.findByCafeEntityAndNameContainingIgnoreCaseAndStatusAndIsPublic(cafe, name, status, isPublic);
        } else if (name != null) {
            filteredBenefits = benefitRepository.findByCafeEntityAndNameContainingIgnoreCase(cafe, name);
        } else if (status != null) {
            filteredBenefits = benefitRepository.findByCafeEntityAndStatus(cafe, status);
        } else if (isPublic != null) {
            filteredBenefits = benefitRepository.findByCafeEntityAndIsPublic(cafe, isPublic);
        } else {
            filteredBenefits = benefitRepository.findByCafeEntity(cafe);
        }

        return filteredBenefits.stream().map(responseMapper::toDto).toList();
    }


}