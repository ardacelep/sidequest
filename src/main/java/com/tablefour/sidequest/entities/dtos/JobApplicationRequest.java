package com.tablefour.sidequest.entities.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class JobApplicationRequest {
    @NotNull(message = "Job posting ID is required")
    private UUID jobPostingId;

    @NotBlank(message = "Cover letter is required")
    @Size(min = 10, message = "Cover letter must be at least 50 characters")
    private String coverLetter;
}