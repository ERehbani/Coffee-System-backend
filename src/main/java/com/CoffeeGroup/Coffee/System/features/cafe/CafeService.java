package com.CoffeeGroup.Coffee.System.features.cafe;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.access.CafeAccessGuard;
import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.auth.dto.CreateCafeAccountRequest;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.common.persistence.Specifications;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.CafeResponse;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.dto.UpdateCafeRequest;
import com.CoffeeGroup.Coffee.System.features.cafe.domain.mapper.CafeMapper;
import com.CoffeeGroup.Coffee.System.features.review.ReviewRepository.CafeReviewSummary;
import com.CoffeeGroup.Coffee.System.features.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeService implements ICafeService {

    private static final Sort BY_NAME = Sort.by("name");

    private final CafeRepository cafeRepository;
    private final CafeMapper cafeMapper;
    private final SecurityUtils securityUtils;
    private final CafeAccessGuard cafeAccessGuard;

    @Override
    public List<CafeResponse> getAll() {
        Boolean active = securityUtils.isAdmin() ? null : true;
        return search(null, null, active);
    }

    @Override
    public CafeResponse getMine() {
        return cafeMapper.toResponse(securityUtils.currentCafe());
    }

    @Override
    public CafeResponse getById(UUID externalId) {
        return cafeMapper.toResponse(cafeAccessGuard.visibleCafe(externalId));
    }

    @Override
    @Transactional
    public CafeResponse create(CreateCafeAccountRequest request, UserEntity user) {
        CafeEntity cafe = CafeEntity.builder()
                .name(request.name())
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .description(request.description())
                .active(true)
                .user(user)
                .build();
        cafeRepository.save(cafe);
        return cafeMapper.toResponse(cafe, CafeReviewSummary.NONE);
    }

    @Override
    @Transactional
    public CafeResponse update(UUID externalId, UpdateCafeRequest request) {
        CafeEntity cafe = cafeAccessGuard.managedCafe(externalId);
        cafe.setAddress(request.address());
        cafe.setPhoneNumber(request.phoneNumber());
        cafe.setDescription(request.description());
        cafe.setLogo(request.logo());
        cafe.setImage(request.image());
        if (request.openingHours() != null) {
            cafe.replaceOpeningHours(cafeMapper.toOpeningHours(request.openingHours()));
        }
        return cafeMapper.toResponse(cafe);
    }

    @Override
    @Transactional
    public void delete(UUID externalId) {
        cafeRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Cafe not found"))
                .deactivate();
    }

    @Override
    public List<CafeResponse> filterCafes(String name, String address, Boolean active) {
        Boolean visibleState = securityUtils.isAdmin() ? active : true;
        return search(name, address, visibleState);
    }

    private List<CafeResponse> search(String name, String address, Boolean active) {
        return cafeMapper.toResponses(cafeRepository.findAll(Specifications.allOf(
                CafeSpecifications.nameContains(name),
                CafeSpecifications.addressContains(address),
                CafeSpecifications.hasActive(active)), BY_NAME));
    }
}
