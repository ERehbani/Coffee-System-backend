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

@RestController
@AllArgsConstructor
@RequestMapping("/cafes")
public class CafeController {

    private final ICafeService service;
    private final IUserService userService;

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerCafe(@Valid @RequestBody CreateCafeAccountRequest request) {
     return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCafe(request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @PutMapping("/{externalId}")
    ResponseEntity<CafeResponse> update(@PathVariable UUID externalId, @Valid @RequestBody UpdateCafeRequest request){
      return ResponseEntity.ok(service.update(externalId,request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    ResponseEntity<Void> delete(@PathVariable UUID externalId){
        service.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CLIENTE')")
    @GetMapping
    ResponseEntity<List<CafeResponse>> getAll(){
        return ResponseEntity.ok(service.getAll());
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/{externalId}")
    ResponseEntity<CafeResponse> getByExternalId(@PathVariable UUID externalId){
        return ResponseEntity.ok(service.getById(externalId));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/filter")
    ResponseEntity<List<CafeResponse>> filterCafes(@RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String address,
                                                   @RequestParam(required = false) Boolean state){
        return ResponseEntity.ok(service.filterCafes(name, address, state));
    }
}
