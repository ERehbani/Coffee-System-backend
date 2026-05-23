package com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionUpdateRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IRedemptionUpdateRequest extends IMapper<RedemptionEntity, RedemptionUpdateRequest> {

    RedemptionEntity toEntity(RedemptionUpdateRequest redemptionUpdateRequest);
    RedemptionUpdateRequest toDto(RedemptionEntity redemptionEntity);

}
