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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "Reviews", description = "Endpoints for managing cafe reviews")
public class ReviewController {

    private final IReviewService reviewService;

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PostMapping
    @Operation(summary = "Create review", description = "Creates a new review for a cafeteria")
    @ApiResponses
    ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        return new ResponseEntity<>(reviewService.create(reviewCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all reviews", description = "Retrieves all reviews")
    @ApiResponses
    ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @GetMapping("/cafe/{cafeExternalId}")
    @Operation(summary = "Get reviews by cafe", description = "Retrieves all reviews for a specific cafeteria")
    @ApiResponses
    ResponseEntity<List<ReviewResponse>> getByCafe(@Parameter(description = "Cafe external ID") @PathVariable UUID cafeExternalId) {
        return ResponseEntity.ok(reviewService.getByCafe(cafeExternalId));
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Get review by ID", description = "Retrieves a specific review by its external ID")
    @ApiResponses
    ResponseEntity<ReviewResponse> getById(@Parameter(description = "Review external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(reviewService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update review", description = "Updates an existing review")
    @ApiResponses
    ResponseEntity<ReviewResponse> update(@Parameter(description = "Review external ID") @PathVariable UUID externalId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ResponseEntity.ok(reviewService.update(externalId, reviewUpdateRequest));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete review", description = "Deletes a review by its external ID")
    @ApiResponses
    ResponseEntity<Void> delete(@Parameter(description = "Review external ID") @PathVariable UUID externalId) {
        reviewService.deleteById(externalId);
        return ResponseEntity.noContent().build();
    }

}
