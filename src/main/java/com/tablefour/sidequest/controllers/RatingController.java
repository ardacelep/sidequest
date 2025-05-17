package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.business.abstracts.RatingService;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.dtos.CreateRatingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'EMPLOYER')")
    public ResponseEntity<BaseResponse<Rating>> createRating(@Valid @RequestBody CreateRatingRequest request) {
        return ratingService.createRating(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Rating>> getRatingById(@PathVariable UUID id) {
        return ratingService.getRatingById(id);
    }

    @GetMapping("/rated/{userId}")
    public ResponseEntity<PageResponse<Rating>> getRatingsByRatedUser(
            @PathVariable UUID userId, Pageable pageable) {
        return ratingService.getRatingsByRatedUser(userId, pageable);
    }

    @GetMapping("/rater/{userId}")
    public ResponseEntity<PageResponse<Rating>> getRatingsByRaterUser(
            @PathVariable UUID userId, Pageable pageable) {
        return ratingService.getRatingsByRaterUser(userId, pageable);
    }

    @GetMapping("/average/{userId}")
    public ResponseEntity<BaseResponse<Double>> getAverageRatingForUser(@PathVariable UUID userId) {
        return ratingService.getAverageRatingForUser(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'EMPLOYER')")
    public ResponseEntity<BaseResponse<Void>> deleteRating(@PathVariable UUID id) {
        return ratingService.deleteRating(id);
    }
}