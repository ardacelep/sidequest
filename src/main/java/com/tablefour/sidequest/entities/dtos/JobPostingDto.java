package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingDto {
    private UUID id;
    private UUID employerId;
    private String employerCompanyName;
    private String title;
    private String description;
    private String location;
    private double payment;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private JobStatus status;
    private LocalDateTime createdAt;
    private double aiTrustScore;
    private JobCategory category;
    private int requiredEmployeeCount;
    private boolean isUrgent;
    private String requirements;
    private UUID assignedEmployeeId;
    private String assignedEmployeeName;
    private LocalDateTime lastModifiedAt;
    private boolean isActive;
}