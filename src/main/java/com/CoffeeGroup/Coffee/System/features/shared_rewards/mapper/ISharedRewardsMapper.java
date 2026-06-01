package com.CoffeeGroup.Coffee.System.features.shared_rewards.mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.SharedRewardsEntity;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsRequest;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ISharedRewardsMapper extends IMapper<SharedRewardsEntity, SharedRewardsResponse> {

    @Mapping(ignore = true, target = "benefit")
    @Mapping(ignore = true, target = "transmitter")
    @Mapping(ignore = true, target = "receiver")
    SharedRewardsEntity toEntity(SharedRewardsRequest request);
    @Override
    @Mapping(ignore = true, target = "benefit")
    @Mapping(ignore = true, target = "transmitter")
    @Mapping(ignore = true, target = "receiver")
    SharedRewardsEntity toEntity(SharedRewardsResponse sharedRewardsResponse);

    @Override
    @Mapping(target = "benefit", source = "benefit.externalId")
    @Mapping(target = "transmitter", source = "transmitter.externalId")
    @Mapping(target = "receiver", source = "receiver.externalId")
    SharedRewardsResponse toDto(SharedRewardsEntity sharedRewardsEntity);
}
