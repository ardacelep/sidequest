package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.business.abstracts.JobApplicationService;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.dtos.JobApplicationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<JobApplication>> applyForJob(@Valid @RequestBody JobApplicationRequest request) {
        return jobApplicationService.applyForJob(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<JobApplication>> getApplicationById(@PathVariable UUID id) {
        return jobApplicationService.getApplicationById(id);
    }

    @GetMapping("/job/{jobPostingId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<Page<JobApplication>>> getApplicationsByJobPosting(
            @PathVariable UUID jobPostingId, Pageable pageable) {
        return jobApplicationService.getApplicationsByJobPosting(jobPostingId, pageable);
    }

    @GetMapping("/applicant/{applicantId}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<Page<JobApplication>>> getApplicationsByApplicant(
            @PathVariable UUID applicantId, Pageable pageable) {
        return jobApplicationService.getApplicationsByApplicant(applicantId, pageable);
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobApplication>> acceptApplication(@PathVariable UUID id) {
        return jobApplicationService.acceptApplication(id);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobApplication>> rejectApplication(
            @PathVariable UUID id, @RequestParam String reason) {
        return jobApplicationService.rejectApplication(id, reason);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<BaseResponse<Void>> withdrawApplication(@PathVariable UUID id) {
        return jobApplicationService.withdrawApplication(id);
    }
}