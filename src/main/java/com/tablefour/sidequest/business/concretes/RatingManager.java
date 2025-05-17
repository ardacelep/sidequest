package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.business.abstracts.RatingService;
import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.dataAccess.RatingDao;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.dtos.JobPostingDto;
import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.dtos.CreateRatingRequest;
import com.tablefour.sidequest.entities.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingManager implements RatingService {

        private final RatingDao ratingDao;
        private final UserService userService;
        private final JobPostingService jobPostingService;
        private final BaseResponseHelpers baseResponseHelpers;
        private final WebRequest webRequest;

        @Override
        @Transactional
        public ResponseEntity<BaseResponse<Rating>> createRating(CreateRatingRequest request) {
                User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                // Check if user exists
                var ratedUserResult = userService.getUserById(request.getRatedUserId().toString());
                if (!ratedUserResult.isSuccess()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Rated user not found",
                                        HttpStatus.NOT_FOUND);
                }

                // Check if user has already rated for this job
                if (ratingDao.existsByRaterUserAndJobPosting_Id(currentUser, request.getJobPostingId())) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.ERROR,
                                        "You have already rated for this job",
                                        HttpStatus.BAD_REQUEST);
                }

                // Get the actual JobPosting entity
                Optional<JobPosting> jobPostingEntity = jobPostingService
                                .getJobPostingEntityById(request.getJobPostingId());
                if (jobPostingEntity.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                // Check if job is completed
                JobPosting jobPosting = jobPostingEntity.get();
                if (!JobStatus.COMPLETED.equals(jobPosting.getStatus())) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.ERROR,
                                        "Cannot rate until job is completed",
                                        HttpStatus.BAD_REQUEST);
                }

                // Create and save the rating
                Rating rating = Rating.builder()
                                .ratedUser(ratedUserResult.getData())
                                .raterUser(currentUser)
                                .jobPosting(jobPosting)
                                .value(request.getValue())
                                .comment(request.getComment())
                                .build();

                Rating savedRating = ratingDao.save(rating);

                BaseResponse<Rating> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.CREATED,
                                MessageType.CREATED,
                                "Rating created successfully",
                                webRequest,
                                savedRating);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<BaseResponse<Rating>> getRatingById(UUID id) {
                var rating = ratingDao.findById(id)
                                .orElseThrow(() -> new RuntimeBaseException(
                                                ErrorMessageType.NO_RECORD_EXISTS,
                                                "Rating not found",
                                                HttpStatus.NOT_FOUND));

                BaseResponse<Rating> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Rating retrieved successfully",
                                webRequest,
                                rating);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<Rating>> getRatingsByRatedUser(UUID userId, Pageable pageable) {
                var userResult = userService.getUserById(userId.toString());
                if (!userResult.isSuccess()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "User not found",
                                        HttpStatus.NOT_FOUND);
                }

                Page<Rating> ratings = ratingDao.findByRatedUser(userResult.getData(), pageable);

                PageResponse<Rating> response = PageResponse.of(
                                ratings,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Ratings retrieved successfully",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<Rating>> getRatingsByRaterUser(UUID userId, Pageable pageable) {
                var userResult = userService.getUserById(userId.toString());
                if (!userResult.isSuccess()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "User not found",
                                        HttpStatus.NOT_FOUND);
                }

                Page<Rating> ratings = ratingDao.findByRaterUser(userResult.getData(), pageable);

                PageResponse<Rating> response = PageResponse.of(
                                ratings,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Ratings retrieved successfully",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<BaseResponse<Double>> getAverageRatingForUser(UUID userId) {
                var userResult = userService.getUserById(userId.toString());
                if (!userResult.isSuccess()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "User not found",
                                        HttpStatus.NOT_FOUND);
                }

                Double averageRating = ratingDao.getAverageRatingForUser(userId);
                if (averageRating == null) {
                        averageRating = 0.0;
                }

                BaseResponse<Double> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Average rating retrieved successfully",
                                webRequest,
                                averageRating);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        private void verifyRatingOwnership(Rating rating) {
                User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (!rating.getRaterUser().getId().equals(currentUser.getId())) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.ERROR,
                                        "You are not authorized to modify this rating",
                                        HttpStatus.FORBIDDEN);
                }
        }

        @Override
        @Transactional
        public ResponseEntity<BaseResponse<Void>> deleteRating(UUID id) {
                Optional<Rating> ratingOptional = ratingDao.findById(id);
                if (ratingOptional.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Rating not found",
                                        HttpStatus.NOT_FOUND);
                }

                Rating rating = ratingOptional.get();
                verifyRatingOwnership(rating);
                ratingDao.deleteById(id);

                BaseResponse<Void> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.DELETED,
                                "Rating deleted successfully",
                                webRequest,
                                null);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }
}