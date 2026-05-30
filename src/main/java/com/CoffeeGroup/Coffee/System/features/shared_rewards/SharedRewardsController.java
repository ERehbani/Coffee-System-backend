package com.CoffeeGroup.Coffee.System.features.shared_rewards;


import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsRequest;
import com.CoffeeGroup.Coffee.System.features.shared_rewards.dto.SharedRewardsResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shared-reward")
@AllArgsConstructor
public class SharedRewardsController {
    private SharedRewardsService service;


    @GetMapping
    public ResponseEntity<List<SharedRewardsResponse>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAll());
    }

    @GetMapping("/{sharedRewardId}")
    public ResponseEntity<SharedRewardsResponse> findById(@PathVariable UUID sharedRewardId){
        return ResponseEntity.status(HttpStatus.OK).body(service.findById(sharedRewardId));
    }

    @PostMapping
    public ResponseEntity<SharedRewardsResponse> save(@RequestBody @Valid SharedRewardsRequest sharedReward){
        return ResponseEntity.status(HttpStatus.OK).body(service.save(sharedReward));
    }

    @DeleteMapping("/{sharedRewardId}")
    public ResponseEntity.BodyBuilder delete(@PathVariable UUID sharedRewardId){
        service.delete(sharedRewardId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT);
    }
}
