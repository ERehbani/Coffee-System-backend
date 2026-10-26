package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsEntity;
import com.CoffeeGroup.Coffee.System.auth.credentials.CredentialsRepository;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitCreateMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers.IBenefitResponseMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_format.IBenefitFormatRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.IBenefitTypeRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
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
    private final CredentialsRepository credentialsRepository;
    private final IBenefitFormatRepository benefitFormatRepository;

    @Transactional
    @Override
    public BenefitResponse save(BenefitCreateRequest benefitCreate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        TierEntity tierEntity = tierRepository.findByExternalId(benefitCreate.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(benefitCreate.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));
        if (!benefitFormatRepository.existsByCodeAndActiveTrue(benefitType.getName())) {
            throw new IllegalStateException("The benefit format " + benefitType.getName() + " is not enabled");
        }

        CafeEntity cafeEntity = null;
        if (isCafeteria) {
            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            cafeEntity = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else if (benefitCreate.cafeId() != null) {
            cafeEntity = cafeRepository.findByExternalId(benefitCreate.cafeId())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }

        BenefitEntity benefit = createMapper.toEntity(benefitCreate);
        benefit.setExternalId(UUID.randomUUID());
        benefit.setBenefitType(benefitType);
        benefit.setTierEntity(tierEntity);
        benefit.setCafeEntity(cafeEntity);
        benefit.setIsPublic(false);
        benefit.setStatus(false);
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
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria) {
            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
            return benefitRepository.findByCafeEntity(cafe).stream()
                    .map(responseMapper::toDto)
                    .toList();
        }

        return benefitRepository.findAll().stream().map(responseMapper::toDto).toList();
    }


    public BenefitResponse findById(UUID externalId){
        BenefitEntity benefit = getBenefit(externalId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria) {
            if (benefit.getCafeEntity() == null) {
                throw new AccessDeniedException("Access denied: Cafeterias cannot view global benefits");
            }

            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!benefit.getCafeEntity().getExternalId().equals(cafe.getExternalId())) {
                throw new AccessDeniedException("Access denied: You do not have permission to view this benefit");
            }
        }

        return responseMapper.toDto(benefit);
    }

    @Transactional
    public BenefitResponse confirmBenefit(UUID externalId){
        return setVisibility(externalId,true, true);
    }

    @Transactional
    public BenefitResponse cancelBenefit(UUID externalId){
        return setVisibility(externalId,false, false);
    }

    @Transactional
    public BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest newEndDateRequest){
        BenefitEntity benefit = getBenefit(externalId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria) {
            if (benefit.getCafeEntity() == null) {
                throw new AccessDeniedException("Access denied: Cafeterias cannot modify global benefits");
            }

            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!benefit.getCafeEntity().getExternalId().equals(cafe.getExternalId())) {
                throw new AccessDeniedException("Access denied: You do not have permission to modify this benefit");
            }
        }

        benefit.setEndDate(newEndDateRequest.endDate());
        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    @Transactional
    @Override
    public BenefitResponse update(UUID externalId, BenefitUpdateRequest request) {
        BenefitEntity benefit = getBenefit(externalId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria) {
            if (benefit.getCafeEntity() == null) {
                throw new AccessDeniedException("Access denied: Cafeterias cannot modify global benefits");
            }

            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!benefit.getCafeEntity().getExternalId().equals(cafe.getExternalId())) {
                throw new AccessDeniedException("Access denied: You do not have permission to modify this benefit");
            }
        }

        if (request.startDate() != null && request.endDate() != null
                && request.endDate().isBefore(request.startDate())) {
            throw new IllegalStateException("The end date cannot be before the start date");
        }

        TierEntity tierEntity = tierRepository.findByExternalId(request.tierId())
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
        BenefitTypeEntity benefitType = benefitTypeRepository.findByExternalId(request.typeId())
                .orElseThrow(() -> new EntityNotFoundException("Benefit type not found"));

        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setConditions(request.conditions());
        benefit.setPointsCost(request.pointsCost());
        benefit.setStartDate(request.startDate());
        benefit.setEndDate(request.endDate());
        benefit.setMaxRedemptions(request.maxRedemptions());
        benefit.setTierEntity(tierEntity);
        benefit.setBenefitType(benefitType);

        benefitRepository.save(benefit);
        return responseMapper.toDto(benefit);
    }

    public BenefitEntity getBenefit(UUID externalId){
        BenefitEntity benefitEntity = benefitRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Benefit not found"));
        return benefitEntity;
    }

    private BenefitResponse setVisibility(UUID externalId, boolean isPublic, boolean status){
        BenefitEntity benefit = getBenefit(externalId);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isCafeteria = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CAFETERIA"));

        if (isCafeteria) {
            if (benefit.getCafeEntity() == null) {
                throw new AccessDeniedException("Access denied: Cafeterias cannot modify global benefits");
            }

            String currentUser = securityUtils.getCurrentUsername();
            CredentialsEntity credentials = credentialsRepository.findByUsername(currentUser)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            CafeEntity cafe = cafeRepository.findByUser(credentials.getUsuario())
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));

            if (!benefit.getCafeEntity().getExternalId().equals(cafe.getExternalId())) {
                throw new AccessDeniedException("Access denied: You do not have permission to modify this benefit");
            }
        }

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

    public List<BenefitResponse> filterAvailableBenefits(UUID cafeId, String name, BenefitType type, Integer maxPoints) {

        CafeEntity cafe;

        if (securityUtils.isCafeteria()) {
            String currentUser = securityUtils.getCurrentUsername();
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(cafeId, currentUser)
                    .orElseThrow(() -> new AccessDeniedException("Access denied: You do not have permission to view benefits of this cafe"));
        } else {
            cafe = cafeRepository.findByExternalId(cafeId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        }

        List<BenefitEntity> benefits;

        if (name != null && type != null && maxPoints != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_NameAndPointsCostLessThanEqual(cafe, name, type, maxPoints);
        } else if (name != null && type != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_Name(cafe, name, type);
        } else if (name != null && maxPoints != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndPointsCostLessThanEqual(cafe, name, maxPoints);
        } else if (type != null && maxPoints != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndBenefitType_NameAndPointsCostLessThanEqual(cafe, type, maxPoints);
        } else if (name != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCase(cafe, name);
        } else if (type != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndBenefitType_Name(cafe, type);
        } else if (maxPoints != null) {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrueAndPointsCostLessThanEqual(cafe, maxPoints);
        } else {
            benefits = benefitRepository.findByCafeEntityAndIsPublicTrueAndStatusTrue(cafe);
        }

        return benefits.stream()
                .map(responseMapper::toDto)
                .toList();
    }

    @Override
    public List<BenefitResponse> filterAvailableGlobalBenefits(String name, BenefitType type, Integer maxPoints) {
        GlobalBenefit global = GlobalBenefit.GLOBAL_BENEFIT;
        List<BenefitEntity> benefits;

        if (name != null && type != null && maxPoints != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_NameAndPointsCostLessThanEqual(global, name, type, maxPoints);
        } else if (name != null && type != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_Name(global, name, type);
        } else if (name != null && maxPoints != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndPointsCostLessThanEqual(global, name, maxPoints);
        } else if (type != null && maxPoints != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndBenefitType_NameAndPointsCostLessThanEqual(global, type, maxPoints);
        } else if (name != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCase(global, name);
        } else if (type != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndBenefitType_Name(global, type);
        } else if (maxPoints != null) {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrueAndPointsCostLessThanEqual(global, maxPoints);
        } else {
            benefits = benefitRepository.findByIsGlobalAndIsPublicTrueAndStatusTrue(global);
        }

        return benefits.stream()
                .map(responseMapper::toDto)
                .toList();
    }
}
