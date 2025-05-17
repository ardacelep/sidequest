package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.enums.JobCategory;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateJobPostingRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 20, message = "Description must be at least 20 characters")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Payment is required")
    @Positive(message = "Payment must be positive")
    private Double payment;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @NotNull(message = "Category is required")
    private JobCategory category;

    @Min(value = 1, message = "Required employee count must be at least 1")
    private int requiredEmployeeCount = 1;

    private boolean isUrgent;

    private String requirements;
}