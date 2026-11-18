package com.CoffeeGroup.Coffee.System.features.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.CoffeeGroup.Coffee.System.common.web.PageResponse;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UpdateUserRequest;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
@Tag(name = "Usuarios", description = "Endpoints para administrar usuarios")
public class UserController {

    private final IUserService userService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista los usuarios paginados, los más nuevos primero por defecto")
    public ResponseEntity<PageResponse<UserResponse>> getAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserResponse> getById(@Parameter(description = "ID externo del usuario") @PathVariable UUID externalId) {
        return ResponseEntity.ok(userService.getById(externalId));
    }

    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar usuario", description = "Cambia el email (y el usuario de inicio de sesión) o el estado activo")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "ID externo del usuario") @PathVariable UUID externalId,
            @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(externalId, request));
    }

    @DeleteMapping("/{externalId}")
    @Operation(summary = "Desactivar usuario", description = "Eliminación lógica: el usuario ya no puede iniciar sesión")
    public ResponseEntity<Void> delete(@Parameter(description = "ID externo del usuario") @PathVariable UUID externalId) {
        userService.delete(externalId);
        return ResponseEntity.noContent().build();
    }
}
