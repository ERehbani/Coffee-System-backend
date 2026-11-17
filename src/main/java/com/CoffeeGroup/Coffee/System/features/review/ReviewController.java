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
@Tag(name = "Reseñas", description = "Endpoints para administrar las reseñas de las cafeterías")
public class ReviewController {

    private final IReviewService reviewService;

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PostMapping
    @Operation(summary = "Crear reseña", description = "Crea una reseña nueva para una cafetería")
    @ApiResponses
    ResponseEntity<ReviewResponse> create(@Valid @RequestBody ReviewCreateRequest reviewCreateRequest) {
        return new ResponseEntity<>(reviewService.create(reviewCreateRequest), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todas las reseñas", description = "Obtiene todas las reseñas")
    @ApiResponses
    ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @GetMapping("/cafe/{cafeExternalId}")
    @Operation(summary = "Obtener reseñas por cafetería", description = "Obtiene todas las reseñas de una cafetería")
    @ApiResponses
    ResponseEntity<List<ReviewResponse>> getByCafe(@Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeExternalId) {
        return ResponseEntity.ok(reviewService.getByCafe(cafeExternalId));
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener reseña por ID", description = "Obtiene una reseña por su ID externo")
    @ApiResponses
    ResponseEntity<ReviewResponse> getById(@Parameter(description = "ID externo de la reseña") @PathVariable UUID externalId) {
        return ResponseEntity.ok(reviewService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar reseña", description = "Actualiza una reseña existente")
    @ApiResponses
    ResponseEntity<ReviewResponse> update(@Parameter(description = "ID externo de la reseña") @PathVariable UUID externalId,
            @RequestBody ReviewUpdateRequest reviewUpdateRequest) {
        return ResponseEntity.ok(reviewService.update(externalId, reviewUpdateRequest));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar reseña", description = "Elimina una reseña por su ID externo")
    @ApiResponses
    ResponseEntity<Void> delete(@Parameter(description = "ID externo de la reseña") @PathVariable UUID externalId) {
        reviewService.deleteById(externalId);
        return ResponseEntity.noContent().build();
    }

}
