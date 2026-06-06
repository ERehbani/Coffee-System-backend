package com.CoffeeGroup.Coffee.System.features.benefit.domain.mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.benefit.BenefitEntity;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBenefitResponseMapper {
    @Mapping(target = "cafeEntity",
            expression = "java(benefitEntity.getCafeEntity() != null ? benefitEntity.getCafeEntity().getName() : null)")

    BenefitResponse toDto(BenefitEntity benefitEntity);

}
