package com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;

@Mapper(componentModel = "spring")
public interface IBenefitResponseMapper {
    @Mapping(target = "cafeEntity",
            expression = "java(benefitEntity.getCafeEntity() != null ? benefitEntity.getCafeEntity().getName() : null)")
    @Mapping(target = "benefitType", source = "benefitType.name")
    @Mapping(target = "tierName", source = "tierEntity.name")
    @Mapping(target = "tierMinPoints", source = "tierEntity.minPoints")
    BenefitResponse toDto(BenefitEntity benefitEntity);

}
