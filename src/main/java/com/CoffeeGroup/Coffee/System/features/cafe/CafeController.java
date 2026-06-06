package com.CoffeeGroup.Coffee.System.features.cafe;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.user.IUserService;
import com.CoffeeGroup.Coffee.System.features.user.domain.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/cafes")
public class CafeController {

 private final ICafeService service;
 private final IUserService userService;

 /*
    aca tienen el json por si quieren cargar algo
    {
            "email": "sao@gmail.com",
            "password": "Saocafe123",
            "name": "cafe centro",
            "address": "av Colon 1234",
            "description": "cafeteria de especialidad en el centro",
            "phoneNumber": "2234567890",
            "schedule": "Lunes a sabados de 8 a 20"
    }
*/

    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
 @PostMapping("/register")
 public ResponseEntity<UserResponse> registerCafe(@Valid @RequestBody CreateCafeAccountRequest request) {
     return ResponseEntity.status(HttpStatus.CREATED).body(userService.saveCafe(request));
 }

 @PutMapping("/{externalId}")
 ResponseEntity<CafeResponse> update(@PathVariable UUID externalId, @Valid @RequestBody UpdateCafeRequest request){
      return ResponseEntity.ok(service.update(externalId,request));
    }


 @DeleteMapping("/{externalId}")
  ResponseEntity<Void> delete(@PathVariable UUID externalId){
  service.delete(externalId);
   return ResponseEntity.noContent().build();
 }



    @GetMapping
    ResponseEntity<List<CafeResponse>> getAll(){return ResponseEntity.ok(service.getAll());}

    /*
    @GetMapping("/{externalId}")
    ResponseEntity<CafeResponse> getByExternalId(@PathVariable UUID externalId){
        return ResponseEntity.ok(service.getByExternalId(externalId));
    }

     */




}
