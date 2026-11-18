package com.CoffeeGroup.Coffee.System.features.benefit;

import java.util.List;
import java.util.UUID;

import com.CoffeeGroup.Coffee.System.features.benefit_format.BenefitType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitCreateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitResponse;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.BenefitUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.benefit.domain.dto.NewEndDateRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/benefits")
@RequiredArgsConstructor
@Tag(name = "Beneficios", description = "Endpoints para administrar beneficios y recompensas")
public class BenefitController {

    private final IBenefitService benefitService;

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping
    @Operation(summary = "Crear beneficio", description = "Crea un beneficio nuevo para una cafetería")
    @ApiResponses
    ResponseEntity<BenefitResponse> create(@Valid @RequestBody BenefitCreateRequest CreateRequest){
        return new ResponseEntity<>(benefitService.save(CreateRequest),HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @DeleteMapping("/{externalId}")
    @Operation(summary = "Eliminar beneficio", description = "Elimina un beneficio por su ID externo")
    @ApiResponses
    ResponseEntity<BenefitResponse> delete(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId){
        benefitService.delete(externalId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA','ROLE_CLIENTE')")
    @GetMapping
    @Operation(summary = "Listar todos los beneficios", description = "Obtiene todos los beneficios del sistema")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> listAll(){
        return ResponseEntity.ok(benefitService.getAll());
    }

    @GetMapping("/{externalId}")
    @Operation(summary = "Obtener beneficio por ID", description = "Obtiene un beneficio por su ID externo")
    @ApiResponses
    ResponseEntity<BenefitResponse> getById(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId) {
        return ResponseEntity.ok(benefitService.findById(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/publish")
    @Operation(summary = "Publicar beneficio", description = "Publica un beneficio y lo deja disponible para los clientes")
    @ApiResponses
    ResponseEntity<BenefitResponse> publishBenefit(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.confirmBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/cancel")
    @Operation(summary = "Cancelar beneficio", description = "Cancela un beneficio y lo deja no disponible")
    @ApiResponses
    ResponseEntity<BenefitResponse> cancelBenefit(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId){
        return ResponseEntity.ok(benefitService.cancelBenefit(externalId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PostMapping("/{externalId}/changeEndDate")
    @Operation(summary = "Cambiar la fecha de fin del beneficio", description = "Actualiza la fecha de fin de un beneficio")
    @ApiResponses
    ResponseEntity<BenefitResponse> changeEnd(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId, @Valid @RequestBody NewEndDateRequest newEndDate){
       return ResponseEntity.ok(benefitService.changeEndDate(externalId,newEndDate));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @PutMapping("/{externalId}")
    @Operation(summary = "Actualizar beneficio", description = "Actualiza los datos de un beneficio, incluidas sus condiciones")
    @ApiResponses
    ResponseEntity<BenefitResponse> update(@Parameter(description = "ID externo del beneficio") @PathVariable UUID externalId, @Valid @RequestBody BenefitUpdateRequest request){
        return ResponseEntity.ok(benefitService.update(externalId, request));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/filter")
    @Operation(summary = "Filtrar beneficios por cafetería", description = "Filtra los beneficios de una cafetería según varios criterios")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterBenefits(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "Filtra por nombre") @RequestParam(required = false) String name,
            @Parameter(description = "Filtra por estado") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Filtra por visibilidad pública") @RequestParam(required = false) Boolean isPublic,
            @Parameter(description = "Filtra por tipo de beneficio") @RequestParam(required = false) BenefitType type) {
        return ResponseEntity.ok(benefitService.filterBenefitsByCafe(cafeId, name, status, isPublic, type));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}")
    @Operation(summary = "Obtener beneficios por cafetería", description = "Obtiene todos los beneficios de una cafetería")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> getBenefitsByCafe(@Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId){
        return ResponseEntity.ok(benefitService.getAllByCafe(cafeId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_SUPERADMIN', 'ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/{benefitId}")
    @Operation(summary = "Obtener un beneficio de una cafetería", description = "Obtiene un beneficio de una cafetería")
    @ApiResponses
    ResponseEntity<BenefitResponse> getBenefitByCafe(@Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId, @Parameter(description = "ID externo del beneficio") @PathVariable UUID benefitId){
        return ResponseEntity.ok(benefitService.getBenefitByCafe(cafeId, benefitId));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/cafes/{cafeId}/available/filter")
    @Operation(summary = "Filtrar beneficios disponibles", description = "Filtra los beneficios disponibles para los clientes según varios criterios")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterAvailableBenefits(
            @Parameter(description = "ID externo de la cafetería") @PathVariable UUID cafeId,
            @Parameter(description = "Filtra por nombre") @RequestParam(required = false) String name,
            @Parameter(description = "Filtra por tipo de beneficio") @RequestParam(required = false) BenefitType type,
            @Parameter(description = "Filtra por puntos máximos requeridos") @RequestParam(required = false) Integer maxPoints) {
        return ResponseEntity.ok(benefitService.filterAvailableBenefits(cafeId, name, type, maxPoints));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/global/filter")
    @Operation(summary = "Filtrar beneficios globales", description = "Filtra los beneficios globales disponibles en todas las cafeterías")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> filterAvailableGlobalBenefits(
            @Parameter(description = "Filtra por nombre") @RequestParam(required = false) String name,
            @Parameter(description = "Filtra por tipo de beneficio") @RequestParam(required = false) BenefitType type,
            @Parameter(description = "Filtra por puntos máximos requeridos") @RequestParam(required = false) Integer maxPoints) {
        return ResponseEntity.ok(benefitService.filterAvailableGlobalBenefits(name, type, maxPoints));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CLIENTE','ROLE_SUPERADMIN','ROLE_CAFETERIA')")
    @GetMapping("/available")
    @Operation(summary = "Catálogo de beneficios disponibles", description = "Lista todos los beneficios públicos y activos, con filtro opcional por tipo de beneficio y por la cafetería donde se pueden usar (los propios más los globales)")
    @ApiResponses
    ResponseEntity<List<BenefitResponse>> availableCatalog(
            @Parameter(description = "ID externo de la cafetería") @RequestParam(required = false) UUID cafeId,
            @Parameter(description = "Filtra por tipo de beneficio") @RequestParam(required = false) BenefitType type) {
        return ResponseEntity.ok(benefitService.getAvailableCatalog(cafeId, type));
    }
}