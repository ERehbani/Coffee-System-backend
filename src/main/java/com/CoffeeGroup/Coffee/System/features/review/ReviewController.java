package com.CoffeeGroup.Coffee.System.features.review;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final IReviewService reviewService;

    @PostMapping("/create")
    ResponseEntity<ReviewResponse> createRedemption(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest){
        return new ResponseEntity<>(reviewService.save(reviewCreateRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        reviewService.deleteByid(externalId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{externalId}")
    ResponseEntity<ReviewResponse> findById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(reviewService.findByid(externalId));
    }

    @PutMapping("/{externalId}")
    ResponseEntity<ReviewResponse> update(@PathVariable UUID externalId, @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ResponseEntity.ok(reviewService.update(externalId, reviewUpdateRequest));
    }

    @GetMapping
    ResponseEntity<List<ReviewResponse>> findAll(){
        return ResponseEntity.ok(reviewService.findAll());
    }


}
