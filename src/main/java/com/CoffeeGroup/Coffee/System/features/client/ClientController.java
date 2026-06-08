package com.CoffeeGroup.Coffee.System.features.client;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.CreateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;


    //Elimine el endpoint de "crear" ya que se crea automaticamente con usuario, no tiene sentido
    //crear un cliente aislado de un usuario, por lo tanto se le pega al endpount auth/register

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{externalId}")
    public ResponseEntity<ClientResponse> getById(@PathVariable UUID externalId) {
        return ResponseEntity.ok(clientService.getById(externalId));
    }

    @PutMapping("/{externalId}")
    public ResponseEntity<ClientResponse> update(@PathVariable UUID externalId, @Valid @RequestBody UpdateClientRequest request
    ) {
        return ResponseEntity.ok(clientService.update(externalId, request));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        clientService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

}
