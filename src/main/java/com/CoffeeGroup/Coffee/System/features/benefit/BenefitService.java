package com.CoffeeGroup.Coffee.System.features.benefit;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.mapper.BenefitMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.rules.BenefitRules;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitFormatRepository;
import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.catalog.ICatalogProductService;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductEntity;
import com.CoffeeGroup.Coffee.System.features.product.CafeProductRepository;
import com.CoffeeGroup.Coffee.System.features.tier.TierEntity;
import com.CoffeeGroup.Coffee.System.features.tier.TierRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BenefitService implements IBenefitService {

    private static final Sort BY_NAME = Sort.by("name");
    private static final Sort BY_POINTS = Sort.by("pointsCost", "name");

    private final BenefitRepository benefitRepository;
    private final BenefitFormatRepository benefitFormatRepository;
    private final TierRepository tierRepository;
    private final CafeProductRepository cafeProductRepository;
    private final ICatalogProductService catalogProductService;
    private final BenefitMapper benefitMapper;
    private final BenefitRules benefitRules;
    private final CafeAccessGuard cafeAccessGuard;
    private final SecurityUtils securityUtils;
    private final Clock clock;

    @Override
    @Transactional
    public BenefitResponse save(BenefitCreateRequest request) {
        BenefitEntity benefit = BenefitEntity.builder()
                .cafe(owningCafeFor(request.cafeId()))
                .active(false)
                .isPublic(false)
                .name(request.name())
                .description(request.description())
                .conditions(request.conditions())
                .pointsCost(request.pointsCost())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .maxRedemptions(request.maxRedemptions())
                .maxPerClient(request.maxPerClient())
                .tier(findTier(request.tierId()))
                .build();
        configureRule(benefit, request.type(),
                new RuleSettings(request.discountPercentage(), request.productId(), request.catalogProductId()));
        benefitRepository.save(benefit);
        return benefitMapper.toResponse(benefit);
    }

    @Override
    @Transactional
    public BenefitResponse update(UUID externalId, BenefitUpdateRequest request) {
        BenefitEntity benefit = managedBenefit(externalId);
        if (request.maxRedemptions() != null && request.maxRedemptions() < benefit.getRedeemedCount()) {
            throw new BusinessRuleException("The limit cannot be lower than the redemptions already made");
        }
        benefit.setName(request.name());
        benefit.setDescription(request.description());
        benefit.setConditions(request.conditions());
        benefit.setPointsCost(request.pointsCost());
        benefit.setStartDate(request.startDate());
        benefit.setEndDate(request.endDate());
        benefit.setMaxRedemptions(request.maxRedemptions());
        benefit.setMaxPerClient(request.maxPerClient());
        benefit.setTier(findTier(request.tierId()));
        configureRule(benefit, request.type(),
                new RuleSettings(request.discountPercentage(), request.productId(), request.catalogProductId()));
        return benefitMapper.toResponse(benefit);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        BenefitEntity benefit = findBenefit(externalId);
        boolean adminDeletingGlobal = securityUtils.isAdmin() && benefit.isGlobal();
        boolean cafeDeletingOwn = securityUtils.isCafe() && benefit.belongsTo(securityUtils.currentCafe());
        if (!adminDeletingGlobal && !cafeDeletingOwn) {
            throw new AccessDeniedException("Access denied: You do not have permission to delete this benefit");
        }
        benefitRepository.delete(benefit);
        benefitRepository.flush();
    }

    @Override
    public List<BenefitResponse> getAll() {
        if (securityUtils.isCafe()) {
            return search(BenefitSpecifications.belongsTo(securityUtils.currentCafe()), BY_NAME);
        }
        if (securityUtils.isClient()) {
            return search(BenefitSpecifications.isPublished(), BY_NAME);
        }
        return search(null, BY_NAME);
    }

    @Override
    public BenefitResponse findById(UUID externalId) {
        BenefitEntity benefit = findBenefit(externalId);
        if (securityUtils.isCafe() && !benefit.belongsTo(securityUtils.currentCafe())) {
            throw new AccessDeniedException("Access denied: You do not have permission to view this benefit");
        }
        if (securityUtils.isClient() && !benefit.isPublished()) {
            throw new EntityNotFoundException("Benefit not found");
        }
        return benefitMapper.toResponse(benefit);
    }

    @Override
    @Transactional
    public BenefitResponse confirmBenefit(UUID externalId) {
        BenefitEntity benefit = managedBenefit(externalId);
        benefit.publish();
        return benefitMapper.toResponse(benefit);
    }

    @Override
    @Transactional
    public BenefitResponse cancelBenefit(UUID externalId) {
        BenefitEntity benefit = managedBenefit(externalId);
        benefit.unpublish();
        return benefitMapper.toResponse(benefit);
    }

    @Override
    @Transactional
    public BenefitResponse changeEndDate(UUID externalId, NewEndDateRequest request) {
        BenefitEntity benefit = managedBenefit(externalId);
        benefit.setEndDate(request.endDate());
        ensureValidDates(benefit);
        return benefitMapper.toResponse(benefit);
    }

    @Override
    public List<BenefitResponse> getAllByCafe(UUID cafeExternalId) {
        return search(BenefitSpecifications.belongsTo(cafeAccessGuard.managedCafe(cafeExternalId)), BY_NAME);
    }

    @Override
    public BenefitResponse getBenefitByCafe(UUID cafeExternalId, UUID benefitExternalId) {
        CafeEntity cafe = cafeAccessGuard.managedCafe(cafeExternalId);
        BenefitEntity benefit = findBenefit(benefitExternalId);
        if (!benefit.belongsTo(cafe)) {
            throw new EntityNotFoundException("Benefit not found");
        }
        return benefitMapper.toResponse(benefit);
    }

    @Override
    public List<BenefitResponse> filterBenefitsByCafe(UUID cafeId, String name, Boolean active, Boolean isPublic,
            BenefitType type) {
        CafeEntity cafe = cafeAccessGuard.managedCafe(cafeId);
        return search(Specifications.allOf(
                BenefitSpecifications.belongsTo(cafe),
                BenefitSpecifications.nameContains(name),
                BenefitSpecifications.hasActive(active),
                BenefitSpecifications.hasPublic(isPublic),
                BenefitSpecifications.ofType(type)), BY_NAME);
    }

    @Override
    public List<BenefitResponse> filterAvailableBenefits(UUID cafeId, String name, BenefitType type, Integer maxPoints) {
        CafeEntity cafe = cafeAccessGuard.visibleCafe(cafeId);
        return search(Specifications.allOf(
                BenefitSpecifications.belongsTo(cafe),
                availableToday(),
                BenefitSpecifications.nameContains(name),
                BenefitSpecifications.ofType(type),
                BenefitSpecifications.costAtMost(maxPoints)), BY_POINTS);
    }

    @Override
    public List<BenefitResponse> filterAvailableGlobalBenefits(String name, BenefitType type, Integer maxPoints) {
        return search(Specifications.allOf(
                BenefitSpecifications.isGlobal(),
                availableToday(),
                BenefitSpecifications.nameContains(name),
                BenefitSpecifications.ofType(type),
                BenefitSpecifications.costAtMost(maxPoints)), BY_POINTS);
    }

    @Override
    public List<BenefitResponse> getAvailableCatalog(UUID cafeId, BenefitType type) {
        return search(Specifications.allOf(
                availableToday(),
                BenefitSpecifications.usableAt(cafeId),
                BenefitSpecifications.ofType(type)), BY_POINTS);
    }

    private Specification<BenefitEntity> availableToday() {
        return Specifications.allOf(
                BenefitSpecifications.isPublished(),
                BenefitSpecifications.isInsideDates(LocalDate.now(clock)),
                BenefitSpecifications.fromActiveCafe());
    }

    private List<BenefitResponse> search(Specification<BenefitEntity> specification, Sort sort) {
        List<BenefitEntity> benefits = specification == null
                ? benefitRepository.findAll(sort)
                : benefitRepository.findAll(specification, sort);
        return benefits.stream().map(benefitMapper::toResponse).toList();
    }

    private void configureRule(BenefitEntity benefit, BenefitType type, RuleSettings settings) {
        benefit.setFormat(enabledFormat(type));
        benefit.setDiscountPercentage(settings.discountPercentage());
        benefit.setCafeProduct(settings.productId() == null ? null : productOfCafe(settings.productId(), benefit));
        benefit.setCatalogProduct(settings.catalogProductId() == null || !benefit.isGlobal()
                ? null
                : catalogProductService.findActive(settings.catalogProductId()));
        ensureValidDates(benefit);
        benefitRules.ruleFor(benefit).validate(benefit);
    }

    private CafeProductEntity productOfCafe(UUID productId, BenefitEntity benefit) {
        if (benefit.isGlobal()) {
            throw new BusinessRuleException("A global benefit uses a catalog product, not the product of a cafe");
        }
        return cafeProductRepository.findByExternalIdAndCafe(productId, benefit.getCafe())
                .orElseThrow(() -> new EntityNotFoundException("Product not found in this cafe"));
    }

    private BenefitFormatEntity enabledFormat(BenefitType type) {
        return benefitFormatRepository.findByCode(type)
                .filter(BenefitFormatEntity::isActive)
                .orElseThrow(() -> new BusinessRuleException("The benefit format " + type + " is not enabled"));
    }

    private static void ensureValidDates(BenefitEntity benefit) {
        if (benefit.getStartDate() != null && benefit.getEndDate() != null
                && benefit.getEndDate().isBefore(benefit.getStartDate())) {
            throw new BusinessRuleException("The end date cannot be before the start date");
        }
    }

    private CafeEntity owningCafeFor(UUID requestedCafeId) {
        if (securityUtils.isCafe()) {
            return securityUtils.currentCafe();
        }
        return requestedCafeId == null ? null : cafeAccessGuard.managedCafe(requestedCafeId);
    }

    private BenefitEntity managedBenefit(UUID externalId) {
        BenefitEntity benefit = findBenefit(externalId);
        cafeAccessGuard.assertCanManage(benefit.getCafe());
        return benefit;
    }

    private BenefitEntity findBenefit(UUID externalId) {
        return benefitRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Benefit not found"));
    }

    private TierEntity findTier(UUID tierId) {
        return tierRepository.findByExternalId(tierId)
                .orElseThrow(() -> new EntityNotFoundException("Tier not found"));
    }

    private record RuleSettings(BigDecimal discountPercentage, UUID productId, UUID catalogProductId) {
    }
}
