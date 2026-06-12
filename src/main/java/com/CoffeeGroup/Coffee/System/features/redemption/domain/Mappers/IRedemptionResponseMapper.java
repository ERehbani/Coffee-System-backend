package com.CoffeeGroup.Coffee.System.features.redemption.domain.Mappers;


import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.redemption.RedemptionEntity;
import com.CoffeeGroup.Coffee.System.features.redemption.domain.dto.RedemptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IRedemptionResponseMapper extends IMapper<RedemptionEntity, RedemptionResponse> {


    @Mapping(target = "client", ignore = true)
    @Mapping(target = "benefit", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "id", ignore = true)
    RedemptionEntity toEntity(RedemptionResponse redemptionResponse);

    @Mapping(target = "client", source = "client.externalId")
    @Mapping(target = "benefit", source = "benefit.externalId")
    RedemptionResponse toDto(RedemptionEntity redemptionEntity);
}
