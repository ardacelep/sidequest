package com.tablefour.sidequest.entities.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateRatingRequest {

    @NotNull(message = "Rated user ID is required")
    private UUID ratedUserId;

    @NotNull(message = "Job related to the rating is required")
    private UUID jobPostingId; // Optional - only needed for job-related ratings

    @NotNull(message = "Rating value is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer value;

    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
}