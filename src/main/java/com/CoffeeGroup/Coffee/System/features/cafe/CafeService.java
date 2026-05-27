package com.CoffeeGroup.Coffee.System.features.cafe;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CreateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CafeService implements ICafeService {

    private final ICafeRepository repository;
    private final CafeMapper mapper;


    @Override
    public List<CafeResponse> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CafeResponse getByExternalId(UUID externalId) {
        return repository.findByExternalId(externalId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
    }

    @Override
    @Transactional
    public CafeResponse create(CreateCafeRequest request) {

        if (repository.existsByEmail(new Email(request.email()))) {
            throw new EntityAlreadyExistsException("Email already exists");
        }

        CafeEntity entity = mapper.toEntity(request);

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    @Transactional
    public CafeResponse update(UUID externalId, UpdateCafeRequest request) {

        CafeEntity cafe = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
         cafe.setAddress(request.address());
         cafe.setPhoneNumber(request.phoneNumber());
         cafe.setDescription(request.description());
         cafe.setSchedule(request.schedule());
         cafe.setImage(request.image());
        return mapper.toResponse(repository.save(cafe));
    }

    @Override
    public void delete(UUID externalId) {

        CafeEntity cafe = repository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        repository.delete(cafe);
    }
}
