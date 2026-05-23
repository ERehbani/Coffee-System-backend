package com.CoffeeGroup.Coffee.System.features.review;

import com.CoffeeGroup.Coffee.System.common.exceptions.EntityNotFoundException;
import com.CoffeeGroup.Coffee.System.features.cafe.CafeEntity;
import com.CoffeeGroup.Coffee.System.features.cafe.ICafeRepository;
import com.CoffeeGroup.Coffee.System.features.client.ClientEntity;
import com.CoffeeGroup.Coffee.System.features.client.IClientRepository;
import com.CoffeeGroup.Coffee.System.features.review.domain.Mappers.IReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.Mappers.IReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewCreateRequest;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewResponse;
import com.CoffeeGroup.Coffee.System.features.review.domain.dto.ReviewUpdateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class ReviewService implements IReviewService {


    private final IClientRepository clientRepository;
    private final ICafeRepository cafeRepository;
    private final IReviewRepository reviewRepository;

    private final IReviewCreateRequest MapReviewCreateRequest;
    private final IReviewResponse MapReviewResponse;



    @Override
    public ReviewResponse save(ReviewCreateRequest reviewCreateRequest) {

        ClientEntity clientAux = clientRepository.findByExternalId(reviewCreateRequest.clientId()).
                orElseThrow(()-> new EntityNotFoundException("The customer not found"));
        CafeEntity cafeAux = cafeRepository.findByExternalId(reviewCreateRequest.cafeId()).
                orElseThrow(() ->  new EntityNotFoundException("The caffe not found"));

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
                .orElseThrow(()-> new EntityNotFoundException("the Review not found"));

       review.setMessage(reviewUpdateRequest.message());
       reviewRepository.save(review);
       return MapReviewResponse.toDto(review);
    }

    @Override
    public ReviewResponse findByid(UUID externalId) {
        return reviewRepository.findByExternalId(externalId).map(MapReviewResponse::toDto).
                orElseThrow(()-> new EntityNotFoundException("Review not found"));
    }

    @Override
    public void deleteByid(UUID externalId) {
        ReviewEntity review = reviewRepository.findByExternalId(externalId)
                .orElseThrow(()-> new EntityNotFoundException("the Review not found"));
        reviewRepository.delete(review);
        System.out.println("delete 100%");
    }

    @Override
    public List<ReviewResponse> findAll() {
        return reviewRepository.findAll().stream().map(MapReviewResponse::toDto).toList();
    }
}
