package com.CoffeeGroup.Coffee.System.features.review;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.BusinessRuleException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityAlreadyExistsException;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseEntity;
import com.CoffeeGroup.Coffee.System.features.purchase.PurchaseRepository;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.mapper.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService implements IReviewService {

    private final CafeRepository cafeRepository;
    private final ReviewRepository reviewRepository;
    private final PurchaseRepository purchaseRepository;
    private final SecurityUtils securityUtils;
    private final ReviewMapper reviewMapper;
    private final Clock clock;

    @Override
    @Transactional
    public ReviewResponse create(ReviewCreateRequest request) {
        ClientEntity client = securityUtils.currentClient();
        CafeEntity cafe = cafeRepository.findByExternalId(request.cafeId())
                .orElseThrow(() -> new EntityNotFoundException("Cafetería no encontrada"));
        PurchaseEntity purchase = purchaseRepository.findByExternalId(request.purchaseId())
                .orElseThrow(() -> new EntityNotFoundException("Compra no encontrada"));
        if (!purchase.belongsTo(client)) {
            throw new AccessDeniedException("Solo puedes reseñar tus propias compras");
        }
        if (!purchase.isFrom(cafe)) {
            throw new BusinessRuleException("La compra no pertenece a esta cafetería");
        }
        if (purchase.getStatus().isCancellation()) {
            throw new BusinessRuleException("No se pueden reseñar las compras canceladas o reembolsadas");
        }
        if (reviewRepository.existsByPurchaseExternalId(purchase.getExternalId())) {
            throw new EntityAlreadyExistsException("Esta compra ya tiene una reseña");
        }
        ReviewEntity review = ReviewEntity.builder()
                .message(request.message())
                .rating(request.rating())
                .date(LocalDate.now(clock))
                .cafe(cafe)
                .client(client)
                .purchase(purchase)
                .build();
        reviewRepository.save(review);
        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse update(UUID externalId, ReviewUpdateRequest request) {
        ReviewEntity review = reviewOfCurrentUser(externalId);
        review.setMessage(request.message());
        return reviewMapper.toResponse(review);
    }

    @Override
    public ReviewResponse getById(UUID externalId) {
        return reviewMapper.toResponse(findReview(externalId));
    }

    @Override
    @Transactional
    public void deleteById(UUID externalId) {
        reviewRepository.delete(reviewOfCurrentUser(externalId));
    }

    @Override
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream().map(reviewMapper::toResponse).toList();
    }

    @Override
    public List<ReviewResponse> getByCafe(UUID cafeExternalId) {
        return reviewRepository.findByCafeExternalId(cafeExternalId).stream().map(reviewMapper::toResponse).toList();
    }

    private ReviewEntity reviewOfCurrentUser(UUID externalId) {
        ReviewEntity review = findReview(externalId);
        if (!securityUtils.isAdmin() && !review.isWrittenBy(securityUtils.currentClient())) {
            throw new AccessDeniedException("Solo puedes modificar tus propias reseñas");
        }
        return review;
    }

    private ReviewEntity findReview(UUID externalId) {
        return reviewRepository.findByExternalId(externalId)
                .orElseThrow(() -> new EntityNotFoundException("Reseña no encontrada"));
    }
}
