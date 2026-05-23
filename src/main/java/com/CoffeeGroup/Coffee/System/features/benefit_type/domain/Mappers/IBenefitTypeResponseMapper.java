package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBenefitTypeResponseMapper extends IMapper<BenefitTypeEntity, BenefitTypeResponse> {
    @Override
    BenefitTypeEntity toEntity(BenefitTypeResponse benefitTypeResponse);

    @Override
    BenefitTypeResponse toDto(BenefitTypeEntity benefitTypeEntity);
}
