package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.dtos.CreateJobPostingRequest;
import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobPostingController {

    private final JobPostingService jobPostingService;

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobPosting>> createJobPosting(
            @Valid @RequestBody CreateJobPostingRequest request) {
        return jobPostingService.createJobPosting(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<JobPosting>> getJobPostingById(@PathVariable UUID id) {
        return jobPostingService.getJobPostingById(id);
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getAllActiveJobPostings(Pageable pageable) {
        return jobPostingService.getAllActiveJobPostings(pageable);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByCategory(
            @PathVariable JobCategory category, Pageable pageable) {
        return jobPostingService.getJobPostingsByCategory(category, pageable);
    }

    @GetMapping("/employer/{employerId}")
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByEmployer(
            @PathVariable UUID employerId, Pageable pageable) {
        return jobPostingService.getJobPostingsByEmployer(employerId, pageable);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobPosting>> updateJobStatus(
            @PathVariable UUID id, @RequestParam JobStatus newStatus) {
        return jobPostingService.updateJobStatus(id, newStatus);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobPosting>> deactivateJobPosting(@PathVariable UUID id) {
        return jobPostingService.deactivateJobPosting(id);
    }

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<JobPosting>>> searchJobPostings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minPayment,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) Boolean isUrgent,
            Pageable pageable) {
        return jobPostingService.searchJobPostings(location, minPayment, category, isUrgent, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<Void>> deleteJobPosting(@PathVariable UUID id) {
        return jobPostingService.deleteJobPosting(id);
    }

    @PutMapping("/{jobId}/assign/{employeeId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<BaseResponse<JobPosting>> assignEmployee(
            @PathVariable UUID jobId, @PathVariable UUID employeeId) {
        return jobPostingService.assignEmployee(jobId, employeeId);
    }
}