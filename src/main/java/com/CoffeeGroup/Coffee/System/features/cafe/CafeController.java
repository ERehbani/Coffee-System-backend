package com.CoffeeGroup.Coffee.System.features.cafe;

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
@Tag(name = "Cafeterías", description = "Endpoints para administrar cafeterías")
public class CafeController {

    private final ICafeService cafeService;
    private final ICafeDashboardService cafeDashboardService;
    private final IUserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Registrar cafetería", description = "Crea la cuenta de la cafetería y la cafetería")
    public ResponseEntity<UserResponse> registerCafe(@Valid @RequestBody CreateCafeAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCafe(request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar cafetería")
    public ResponseEntity<CafeResponse> update(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID externalId,
            @Valid @RequestBody UpdateCafeRequest request) {
        return ResponseEntity.ok(cafeService.update(externalId, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Desactivar cafetería")
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo de la cafetería") @PathVariable UUID externalId) {
        cafeService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping
    @Operation(summary = "Listar cafeterías", description = "Los clientes solo ven las cafeterías activas")
    public ResponseEntity<List<CafeResponse>> getAll() {
        return ResponseEntity.ok(cafeService.getAll());
    }

    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @GetMapping("/me")
    @Operation(summary = "Obtener mi cafetería")
    public ResponseEntity<CafeResponse> getMine() {
        return ResponseEntity.ok(cafeService.getMine());
    }

    @PreAuthorize("hasAuthority('ROLE_CAFETERIA')")
    @GetMapping("/me/dashboard")
    @Operation(summary = "Obtener el dashboard de mi cafetería", description = "Productos, beneficios y actividad de hoy")
    public ResponseEntity<CafeDashboardResponse> getMyDashboard() {
        return ResponseEntity.ok(cafeDashboardService.getMine());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA', 'ROLE_CLIENTE')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener cafetería por ID")
    public ResponseEntity<CafeResponse> getByExternalId(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID externalId) {
        return ResponseEntity.ok(cafeService.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/filter")
    @Operation(summary = "Filtrar cafeterías", description = "Solo el administrador puede filtrar por estado activo")
    public ResponseEntity<List<CafeResponse>> filterCafes(
            @Parameter(description = "Filtra por nombre") @RequestParam(required = false) String name,
            @Parameter(description = "Filtra por dirección") @RequestParam(required = false) String address,
            @Parameter(description = "Filtra por estado activo") @RequestParam(required = false) Boolean state) {
        return ResponseEntity.ok(cafeService.filterCafes(name, address, state));
    }
}
