package com.CoffeeGroup.Coffee.System.features.cafe;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CafeService implements ICafeService {

    private final ICafeRepository repository;
    private final CafeMapper mapper;

    public CafeService(ICafeRepository cafeRepository, CafeMapper mapper) {
        this.repository = cafeRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CafeResponse> getAll() {
        List<CafeEntity> lista = repository.findAll();

        return lista.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CafeResponse getByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cafeteria no encontrada"));
    }

    @Override
    public CafeResponse save(CreateCafeRequest request) {

        /*if (repository.existsByEmail(new Email(request.email()))) {
            throw new EntityAlreadyExistsException("Email already exists");
        }*/

        CafeEntity entity = mapper.toEntity(request);

        CafeEntity savedEntity = repository.save(entity);

        return mapper.toResponse(savedEntity);
    }

    @Override
    public CafeResponse update(UUID externalId, UpdateCafeRequest request) {

        CafeEntity cafe = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafeteria no encontrada"));



         if(request.address() != null && !request.address().isBlank()){
             cafe.setAddress(request.address());
         }

        Email nuevoEmail = new Email(request.email());

        if (!cafe.getEmail().equals(nuevoEmail)) {
            /*if (repository.existsByEmail(nuevoEmail)) {
                throw new EntityAlreadyExistsException("El Email ya existe");
            }*/
            cafe.setEmail(nuevoEmail);
        }

         if(request.phoneNumber() != null && !request.phoneNumber().isBlank()){

             cafe.setPhoneNumber(request.phoneNumber());

         }

         if(request.description() != null && !request.description().isBlank()){
             cafe.setDescription(request.description());
         }

         if(request.schedule() != null && !request.schedule().isBlank()){

             cafe.setSchedule(request.schedule());
         }

         if(request.image() != null && !request.image().isBlank()){
             cafe.setImage(request.image());
         }
        return mapper.toResponse(repository.save(cafe));
    }

    @Override
    public void delete(UUID externalId) {

        CafeEntity cafe = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafeteria no encontrada"));
        repository.delete(cafe);
    }
}
