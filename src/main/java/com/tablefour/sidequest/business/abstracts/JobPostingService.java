package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.dtos.CreateJobPostingRequest;
import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface JobPostingService {
    ResponseEntity<BaseResponse<JobPosting>> createJobPosting(CreateJobPostingRequest request);

    ResponseEntity<BaseResponse<JobPosting>> getJobPostingById(UUID id);

    ResponseEntity<BaseResponse<Page<JobPosting>>> getAllActiveJobPostings(Pageable pageable);

    ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByCategory(JobCategory category, Pageable pageable);

    ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByEmployer(UUID employerId, Pageable pageable);

    ResponseEntity<BaseResponse<JobPosting>> updateJobStatus(UUID id, JobStatus newStatus);

    ResponseEntity<BaseResponse<JobPosting>> deactivateJobPosting(UUID id);

    ResponseEntity<BaseResponse<Page<JobPosting>>> searchJobPostings(String location, Double minPayment,
            JobCategory category, Boolean isUrgent, Pageable pageable);

    ResponseEntity<BaseResponse<Void>> deleteJobPosting(UUID id);

    ResponseEntity<BaseResponse<JobPosting>> assignEmployee(UUID jobId, UUID employeeId);
}