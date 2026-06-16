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
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping("/cafes")
@Tag(name = "Cafes", description = "Endpoints for managing cafeterias")
public class CafeController {

    private final ICafeService service;
    private final IUserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/register")
    @Operation(summary = "Register cafe", description = "Registers a new cafeteria account")
    @ApiResponses
    public ResponseEntity<UserResponse> registerCafe(@Valid @RequestBody CreateCafeAccountRequest request) {
     return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCafe(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Update cafe", description = "Updates cafeteria information")
    @ApiResponses
    ResponseEntity<CafeResponse> update(@Parameter(description = "Cafe external ID") @PathVariable UUID externalId, @Valid @RequestBody UpdateCafeRequest request){
      return ResponseEntity.ok(service.update(externalId,request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Delete cafe", description = "Deletes a cafeteria by its external ID")
    @ApiResponses
    ResponseEntity<Void> delete(@Parameter(description = "Cafe external ID") @PathVariable UUID externalId){
        service.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping
    @Operation(summary = "List all cafes", description = "Retrieves all cafeterias")
    @ApiResponses
    ResponseEntity<List<CafeResponse>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

//    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/{externalId}")
    @Operation(summary = "Get cafe by ID", description = "Retrieves a specific cafeteria by its external ID")
    @ApiResponses
    ResponseEntity<CafeResponse> getByExternalId(@Parameter(description = "Cafe external ID") @PathVariable UUID externalId){
        return ResponseEntity.ok(service.getById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping("/filter")
    @Operation(summary = "Filter cafes", description = "Filters cafeterias by name, address, or state")
    @ApiResponses
    ResponseEntity<List<CafeResponse>> filterCafes(@Parameter(description = "Filter by name") @RequestParam(required = false) String name,
                                                   @Parameter(description = "Filter by address") @RequestParam(required = false) String address,
                                                   @Parameter(description = "Filter by active state") @RequestParam(required = false) Boolean state){
        return ResponseEntity.ok(service.filterCafes(name, address, state));
    }
}
