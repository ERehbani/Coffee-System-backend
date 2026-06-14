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

    List<BenefitEntity> findDistinctByRedemptionListClient(ClientEntity client);

    // Metodos para filtrar
    List<BenefitEntity> findDistinctByRedemptionList_ClientAndNameContainingIgnoreCaseAndStatusAndBenefitType_Name(ClientEntity client, String name, Boolean status, BenefitType type);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndNameContainingIgnoreCaseAndStatus(ClientEntity client, String name, Boolean status);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndNameContainingIgnoreCaseAndBenefitType_Name(ClientEntity client, String name, BenefitType type);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndStatusAndBenefitType_Name(ClientEntity client, Boolean status, BenefitType type);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndNameContainingIgnoreCase(ClientEntity client, String name);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndStatus(ClientEntity client, Boolean status);

    List<BenefitEntity> findDistinctByRedemptionList_ClientAndBenefitType_Name(ClientEntity client, BenefitType type);

    List<BenefitEntity> findDistinctByRedemptionList_Client(ClientEntity client);


}
