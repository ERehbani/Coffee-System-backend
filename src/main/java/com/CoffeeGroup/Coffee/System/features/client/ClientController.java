package com.CoffeeGroup.Coffee.System.features.client;

import com.CoffeeGroup.Coffee.System.features.client.dto.ClientResponse;
import com.CoffeeGroup.Coffee.System.features.client.dto.CreateClientRequest;
import com.CoffeeGroup.Coffee.System.features.client.dto.UpdateClientRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> findAll() {
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

    @DeleteMapping("/{externalId}")
    public ResponseEntity<Void> delete(@PathVariable UUID externalId) {
        clientService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

}
