package com.CoffeeGroup.Coffee.System.features.benefit_type.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit_type.BenefitTypeEntity;
import com.CoffeeGroup.Coffee.System.features.benefit_type.domain.dto.BenefitTypeCreate;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IBenefitTypeCreateMapper  extends IMapper<BenefitTypeEntity, BenefitTypeCreate> {
    @Override
    BenefitTypeEntity toEntity(BenefitTypeCreate benefitTypeCreate);
    @Override
    BenefitTypeCreate toDto(BenefitTypeEntity benefitTypeEntity);
}
