package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.dtos.JobApplicationRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface JobApplicationService {
    ResponseEntity<BaseResponse<JobApplication>> applyForJob(JobApplicationRequest request);

    ResponseEntity<BaseResponse<JobApplication>> getApplicationById(UUID id);

    ResponseEntity<PageResponse<JobApplication>> getApplicationsByJobPosting(UUID jobPostingId,
            Pageable pageable);

    ResponseEntity<PageResponse<JobApplication>> getApplicationsByApplicant(UUID applicantId, Pageable pageable);

    ResponseEntity<BaseResponse<JobApplication>> acceptApplication(UUID applicationId);

    ResponseEntity<BaseResponse<JobApplication>> rejectApplication(UUID applicationId, String reason);

    ResponseEntity<BaseResponse<Void>> withdrawApplication(UUID applicationId);
}