package com.CoffeeGroup.Coffee.System.features.shared_rewards.mapper;

import com.CoffeeGroup.Coffee.System.common.model.IMapper;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.SharedRewardsEntity;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsRequest;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsResponse;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ISharedRewardsMapper extends IMapper<SharedRewardsEntity, SharedRewardsResponse> {

    SharedRewardsEntity toEntity(SharedRewardsRequest request);
    @Override
    SharedRewardsEntity toEntity(SharedRewardsResponse sharedRewardsResponse);

    @Override
    SharedRewardsResponse toDto(SharedRewardsEntity sharedRewardsEntity);
}
