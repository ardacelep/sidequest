package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.dtos.CreateRatingRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface RatingService {
    ResponseEntity<BaseResponse<Rating>> createRating(CreateRatingRequest request);

    ResponseEntity<BaseResponse<Rating>> getRatingById(UUID id);

    ResponseEntity<PageResponse<Rating>> getRatingsByRatedUser(UUID userId, Pageable pageable);

    ResponseEntity<PageResponse<Rating>> getRatingsByRaterUser(UUID userId, Pageable pageable);

    ResponseEntity<BaseResponse<Double>> getAverageRatingForUser(UUID userId);

    ResponseEntity<BaseResponse<Void>> deleteRating(UUID id);
}