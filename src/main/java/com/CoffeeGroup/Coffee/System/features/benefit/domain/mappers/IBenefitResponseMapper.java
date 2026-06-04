package com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBenefitResponseMapper extends IMapper<BenefitEntity, BenefitResponse> {
    @Override
    BenefitResponse toDto(BenefitEntity benefitEntity);
}
