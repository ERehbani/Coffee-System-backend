package com.CoffeeGroup.Coffee.System.features.role;

import com.CoffeeGroup.Coffee.System.features.role.domain.dto.CreateRoleRequest;
import com.CoffeeGroup.Coffee.System.features.role.domain.dto.RoleResponse;
import com.CoffeeGroup.Coffee.System.features.role.domain.mapper.RoleMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(roleService.getById(externalId));
    }

    @PostMapping
    public ResponseEntity<RoleResponse> create (@Valid @RequestBody CreateRoleRequest request){
        return ResponseEntity.ok(roleService.create(request));
    }

    @DeleteMapping("/{externalId}")
    public ResponseEntity<RoleResponse> delete(@PathVariable UUID externalId) {
        roleService.delete(externalId);
        return ResponseEntity.ok().build();
    }

}
