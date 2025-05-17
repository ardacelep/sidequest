package com.tablefour.sidequest.core.mappers;

import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.dtos.JobPostingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.stream.Collectors;

public class JobPostingMapper {

    public static JobPostingDto toDto(JobPosting jobPosting) {
        if (jobPosting == null) {
            return null;
        }

        return JobPostingDto.builder()
                .id(jobPosting.getId())
                .employerId(jobPosting.getEmployer().getId())
                .employerCompanyName(jobPosting.getEmployer().getCompanyName())
                .title(jobPosting.getTitle())
                .description(jobPosting.getDescription())
                .location(jobPosting.getLocation())
                .payment(jobPosting.getPayment())
                .startDate(jobPosting.getStartDate())
                .endDate(jobPosting.getEndDate())
                .status(jobPosting.getStatus())
                .createdAt(jobPosting.getCreatedAt())
                .aiTrustScore(jobPosting.getAiRecommendation())
                .category(jobPosting.getCategory())
                .requiredEmployeeCount(jobPosting.getRequiredEmployeeCount())
                .isUrgent(jobPosting.isUrgent())
                .requirements(jobPosting.getRequirements())
                .assignedEmployeeId(
                        jobPosting.getAssignedEmployee() != null ? jobPosting.getAssignedEmployee().getId() : null)
                .assignedEmployeeName(
                        jobPosting.getAssignedEmployee() != null ? jobPosting.getAssignedEmployee().getFirstName() + " "
                                + jobPosting.getAssignedEmployee().getLastName() : null)
                .lastModifiedAt(jobPosting.getLastModifiedAt())
                .isActive(jobPosting.isActive())
                .build();
    }

    public static List<JobPostingDto> toDtoList(List<JobPosting> jobPostings) {
        return jobPostings.stream()
                .map(JobPostingMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Page<JobPostingDto> toDtoPage(Page<JobPosting> jobPostings) {
        List<JobPostingDto> dtos = toDtoList(jobPostings.getContent());
        return new PageImpl<>(dtos, jobPostings.getPageable(), jobPostings.getTotalElements());
    }
}