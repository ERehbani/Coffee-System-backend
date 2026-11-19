package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeDashboardResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cafes")
@Tag(name = "Cafes", description = "Endpoints for managing cafeterias")
public class CafeController {

    private final ICafeService cafeService;
    private final ICafeDashboardService cafeDashboardService;
    private final IUserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Register cafe", description = "Creates the cafe account and its cafe")
    public ResponseEntity<UserResponse> registerCafe(@Valid @RequestBody CreateCafeAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCafe(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update cafe")
    public ResponseEntity<CafeResponse> update(
            @Parameter(description = "Cafe external ID") @PathVariable UUID externalId,
            @Valid @RequestBody UpdateCafeRequest request) {
        return ResponseEntity.ok(cafeService.update(externalId, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Desactivar cafetería",
            description = "Oculta la cafetería y bloquea su cuenta, sus ventas y el canje de sus beneficios")
    public ResponseEntity<Void> delete(@Parameter(description = "Cafe external ID") @PathVariable UUID externalId) {
        cafeService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PatchMapping("/{externalId}/activate")
    @Operation(summary = "Reactivar cafetería", description = "Deshace la desactivación de la cafetería y de su cuenta")
    public ResponseEntity<CafeResponse> reactivate(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID externalId) {
        return ResponseEntity.ok(cafeService.reactivate(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping
    @Operation(summary = "List cafes", description = "Clients only see active cafes")
    public ResponseEntity<List<CafeResponse>> getAll() {
        return ResponseEntity.ok(cafeService.getAll());
    }

    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @GetMapping("/me")
    @Operation(summary = "Get my cafe")
    public ResponseEntity<CafeResponse> getMine() {
        return ResponseEntity.ok(cafeService.getMine());
    }

    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @GetMapping("/me/dashboard")
    @Operation(summary = "Get the dashboard of my cafe", description = "Products, benefits and today's activity")
    public ResponseEntity<CafeDashboardResponse> getMyDashboard() {
        return ResponseEntity.ok(cafeDashboardService.getMine());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get cafe by ID")
    public ResponseEntity<CafeResponse> getByExternalId(
            @Parameter(description = "Cafe external ID") @PathVariable UUID externalId) {
        return ResponseEntity.ok(cafeService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/filter")
    @Operation(summary = "Filter cafes", description = "Only the admin can filter by the active state")
    public ResponseEntity<List<CafeResponse>> filterCafes(
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by address") @RequestParam(required = false) String address,
            @Parameter(description = "Filter by active state") @RequestParam(required = false) Boolean state) {
        return ResponseEntity.ok(cafeService.filterCafes(name, address, state));
    }
}
