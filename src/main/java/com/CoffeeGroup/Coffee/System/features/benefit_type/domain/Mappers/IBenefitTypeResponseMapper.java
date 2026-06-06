package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBenefitTypeResponseMapper {
    @Mapping(target = "product", source = "product.name")
    @Mapping(target = "cafeEntity", source = "cafeEntity.name")
    BenefitTypeResponse toDto(BenefitTypeEntity benefitTypeEntity);
}
