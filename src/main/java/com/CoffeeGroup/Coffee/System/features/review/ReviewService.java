package com.CoffeeGroup.Coffee.System.features.review;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.CoffeeGroup.Coffee.System.auth.config.securityconfig.SecurityUtils;
import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.review.domain.Mappers.IReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.Mappers.IReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ReviewService implements IReviewService {


    private final CafeRepository cafeRepository;
    private final IReviewRepository reviewRepository;
    private final SecurityUtils securityUtils;

    private final IReviewCreateRequest MapReviewCreateRequest;
    private final IReviewResponse MapReviewResponse;



    @Override
    public ReviewResponse create(ReviewCreateRequest reviewCreateRequest) {

        ClientEntity clientAux = securityUtils.getCurrentClient();
        if (clientAux == null) {
            throw new EntityNotFoundException("Client not found for current user");
        }
        CafeEntity cafeAux = cafeRepository.findByExternalId(reviewCreateRequest.cafeId()).
                orElseThrow(() ->  new EntityNotFoundException("Cafe not found"));

        ReviewEntity review = MapReviewCreateRequest.toEntity(reviewCreateRequest);

        review.setDate(LocalDate.now());
        review.setCafe(cafeAux);
        review.setClient(clientAux);
        review.setExternalId(UUID.randomUUID());
        reviewRepository.save(review);

        return MapReviewResponse.toDto(review);
    }

    @Override
    public ReviewResponse update(UUID externalId, ReviewUpdateRequest reviewUpdateRequest) {
        ReviewEntity review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Review not found"));

       review.setMessage(reviewUpdateRequest.message());
       reviewRepository.save(review);
       return MapReviewResponse.toDto(review);
    }

    @Override
    public ReviewResponse getById(UUID externalId) {
        return reviewRepository.findByExternalId(externalId).map(MapReviewResponse::toDto).
                orElseThrow(()-> new EntityNotFoundException("Review not found"));
    }

    @Override
    public void deleteById(UUID externalId) {
        ReviewEntity review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream().map(MapReviewResponse::toDto).toList();
    }
}
