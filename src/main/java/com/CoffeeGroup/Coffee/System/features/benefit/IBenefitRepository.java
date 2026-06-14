package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.TypeBenefit.BenefitType;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.Enum.GlobalBenefit;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;

public interface IBenefitRepository extends JpaRepository<BenefitEntity, Long> {
    Optional<BenefitEntity> findByExternalId(UUID externalId);
    List<BenefitEntity> findByIsGlobal(GlobalBenefit isGlobal);
    List<BenefitEntity> findByCafeEntity(CafeEntity cafe);
    Optional<BenefitEntity> findByCafeEntityAndExternalId(CafeEntity cafe, UUID benefitExternalId);

    List<BenefitEntity> findByCafeEntityAndNameContainingIgnoreCase(CafeEntity cafe, String name);

    List<BenefitEntity> findByCafeEntityAndStatus(CafeEntity cafe, Boolean status);

    List<BenefitEntity> findByCafeEntityAndIsPublic(CafeEntity cafe, Boolean isPublic);

    List<BenefitEntity> findByCafeEntityAndBenefitType_Name(CafeEntity cafe, BenefitType type);

    List<BenefitEntity> findByCafeEntityAndNameContainingIgnoreCaseAndStatusAndIsPublic(
            CafeEntity cafe, String name, Boolean status, Boolean isPublic);

    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrue(CafeEntity cafe);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCase(CafeEntity cafe, String name);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndBenefitType_Name(CafeEntity cafe, BenefitType type);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndPointsCostLessThanEqual(CafeEntity cafe, Integer maxPoints);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_Name(CafeEntity cafe, String name, BenefitType type);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndPointsCostLessThanEqual(CafeEntity cafe, String name, Integer maxPoints);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndBenefitType_NameAndPointsCostLessThanEqual(CafeEntity cafe, BenefitType type, Integer maxPoints);
    List<BenefitEntity> findByCafeEntityAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_NameAndPointsCostLessThanEqual(CafeEntity cafe, String name, BenefitType type, Integer maxPoints);

    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrue(GlobalBenefit isGlobal);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCase(GlobalBenefit isGlobal, String name);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndBenefitType_Name(GlobalBenefit isGlobal, BenefitType type);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndPointsCostLessThanEqual(GlobalBenefit isGlobal, Integer maxPoints);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_Name(GlobalBenefit isGlobal, String name, BenefitType type);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndPointsCostLessThanEqual(GlobalBenefit isGlobal, String name, Integer maxPoints);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndBenefitType_NameAndPointsCostLessThanEqual(GlobalBenefit isGlobal, BenefitType type, Integer maxPoints);
    List<BenefitEntity> findByIsGlobalAndIsPublicTrueAndStatusTrueAndNameContainingIgnoreCaseAndBenefitType_NameAndPointsCostLessThanEqual(GlobalBenefit isGlobal, String name, BenefitType type, Integer maxPoints);
}
