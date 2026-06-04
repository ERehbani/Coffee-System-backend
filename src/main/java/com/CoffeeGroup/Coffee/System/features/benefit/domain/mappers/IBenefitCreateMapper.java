package com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBenefitCreateMapper extends IMapper<BenefitEntity, BenefitCreateRequest> {
    @Override
    @Mapping(target = "tierEntity", ignore = true)
    @Mapping(target = "cafeEntity", ignore = true)
    @Mapping(target = "benefitType", ignore = true)
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "isPublic", ignore = true)
    BenefitEntity toEntity(BenefitCreateRequest benefitCreateRequest);
}
