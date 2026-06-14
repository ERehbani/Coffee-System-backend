package com.CoffeeGroup.Coffee.System.features.review;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final IReviewService reviewService;

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PostMapping
    ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        return new ResponseEntity<>(reviewService.create(reviewCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @GetMapping("/{externalId}")
    ResponseEntity<ReviewResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(reviewService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PutMapping("/{externalId}")
    ResponseEntity<ReviewResponse> update(@PathVariable UUID externalId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ResponseEntity.ok(reviewService.update(externalId, reviewUpdateRequest));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        reviewService.deleteById(externalId);
        return ResponseEntity.noContent().build();
    }

}
