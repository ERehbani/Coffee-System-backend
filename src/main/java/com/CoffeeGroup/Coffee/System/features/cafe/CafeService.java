package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.model.Email;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CafeService implements ICafeService {

    private final CafeRepository cafeRepository;
    private final CafeMapper mapper;


    @Override
    public List<CafeResponse> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_SUPERADMIN"));

        List<CafeEntity> cafes;
        if (isAdmin) {
            cafes = cafeRepository.findAll();
        } else {
            cafes = cafeRepository.findByState(true);
        }

        return cafes.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CafeResponse getById(UUID externalId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_SUPERADMIN"));

        CafeEntity cafe;
        if (isAdmin) {
            cafe = cafeRepository.findByExternalId(externalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else {
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(externalId, auth.getName())
                    .orElseThrow(() -> new AccessDeniedException("Access denied"));
        }
        return mapper.toResponse(cafe);
    }


    public CafeEntity create(CreateCafeAccountRequest request, UserEntity user) {

        Email cafeEmail = new Email(request.email());

        if (cafeRepository.existsByEmail(cafeEmail)) {
            throw new EntityAlreadyExistsException("Cafe email already exists");
        }

        CafeEntity cafe = CafeEntity.builder()
                .externalId(UUID.randomUUID())
                .name(request.name())
                .address(request.address())
                .email(cafeEmail)
                .phoneNumber(request.phoneNumber())
                .description(request.description())
                .state(true)
                .image(null)
                .schedule(request.schedule())
                .user(user)
                .build();

        return cafeRepository.save(cafe);
    }

    @Override
    @Transactional
    public CafeResponse update(UUID externalId, UpdateCafeRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_SUPERADMIN"));

        CafeEntity cafe;
        if (isAdmin) {
            cafe = cafeRepository.findByExternalId(externalId)
                    .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        } else {
            cafe = cafeRepository.findByExternalIdAndUser_Credentials_Username(externalId, auth.getName())
                    .orElseThrow(() -> new AccessDeniedException("Access denied"));
        }
         cafe.setAddress(request.address());
         cafe.setPhoneNumber(request.phoneNumber());
         cafe.setDescription(request.description());
         cafe.setSchedule(request.schedule());
         cafe.setImage(request.image());
        return mapper.toResponse(cafeRepository.save(cafe));
    }

    @Override
    public void delete(UUID externalId) {

        CafeEntity cafe = cafeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"));
        cafe.setState(false);
        cafeRepository.save(cafe);
    }

    @Override
    public List<CafeResponse> filterCafes(String name, String address, Boolean state) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_SUPERADMIN"));

        // If not admin and state is not provided, default to active cafes only
        if (!isAdmin && state == null) {
            state = true;
        }

        List<CafeEntity> filteredCafes;

        if (name != null && address != null && state != null) {
            filteredCafes = cafeRepository.findByNameContainingIgnoreCaseAndAddressContainingIgnoreCaseAndState(name, address, state);
        } else if (name != null && address != null) {
            filteredCafes = cafeRepository.findByNameContainingIgnoreCaseAndAddressContainingIgnoreCase(name, address);
        } else if (name != null && state != null) {
            filteredCafes = cafeRepository.findByNameContainingIgnoreCaseAndState(name, state);
        } else if (address != null && state != null) {
            filteredCafes = cafeRepository.findByAddressContainingIgnoreCaseAndState(address, state);
        } else if (name != null) {
            filteredCafes = cafeRepository.findByNameContainingIgnoreCase(name);
        } else if (address != null) {
            filteredCafes = cafeRepository.findByAddressContainingIgnoreCase(address);
        } else if (state != null) {
            filteredCafes = cafeRepository.findByState(state);
        } else {
            filteredCafes = cafeRepository.findAll();
        }

        return filteredCafes.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
