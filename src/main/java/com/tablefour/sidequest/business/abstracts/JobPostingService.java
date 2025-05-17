package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.dtos.CreateJobPostingRequest;
import com.tablefour.sidequest.entities.dtos.JobPostingDateSearchRequest;
import com.tablefour.sidequest.entities.dtos.JobPostingDto;
import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.Optional;

public interface JobPostingService {
        ResponseEntity<BaseResponse<JobPostingDto>> createJobPosting(CreateJobPostingRequest request);

        ResponseEntity<BaseResponse<JobPostingDto>> getJobPostingById(UUID id);

        // Internal method to get JobPosting entity
        Optional<JobPosting> getJobPostingEntityById(UUID id);

        ResponseEntity<PageResponse<JobPostingDto>> getAllActiveJobPostings(Pageable pageable);

        ResponseEntity<PageResponse<JobPostingDto>> getJobPostingsByCategory(JobCategory category, Pageable pageable);

        ResponseEntity<PageResponse<JobPostingDto>> getJobPostingsByEmployer(UUID employerId, Pageable pageable);

        ResponseEntity<BaseResponse<JobPostingDto>> updateJobStatus(UUID id, JobStatus newStatus);

        ResponseEntity<BaseResponse<JobPostingDto>> deactivateJobPosting(UUID id);

        ResponseEntity<PageResponse<JobPostingDto>> searchJobPostings(String location, Double minPayment,
                        JobCategory category, Boolean isUrgent, Pageable pageable);

        ResponseEntity<BaseResponse<Void>> deleteJobPosting(UUID id);

        ResponseEntity<BaseResponse<JobPostingDto>> assignEmployee(UUID jobId, UUID employeeId);

        ResponseEntity<PageResponse<JobPostingDto>> searchJobPostingsByKeyword(String keyword, Pageable pageable);

        ResponseEntity<PageResponse<JobPostingDto>> searchJobPostingsByDate(JobPostingDateSearchRequest request,
                        Pageable pageable);
}