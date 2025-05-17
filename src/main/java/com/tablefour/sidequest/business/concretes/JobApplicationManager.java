package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.JobApplicationService;
import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.dataAccess.JobApplicationDao;
import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.dtos.JobApplicationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobApplicationManager implements JobApplicationService {

    private final JobApplicationDao jobApplicationDao;
    private final JobPostingService jobPostingService;
    private final BaseResponseHelpers baseResponseHelpers;
    private final WebRequest webRequest;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobApplication>> applyForJob(JobApplicationRequest request) {
        UserEmployee currentUser = (UserEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = jobPostingService
                .getJobPostingById(request.getJobPostingId());
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingResponse.getBody().getData().get("jobPosting");

        if (jobApplicationDao.existsByJobPostingAndApplicant(jobPosting, currentUser)) {
            throw new RuntimeBaseException(
                    ErrorMessageType.ERROR,
                    "You have already applied for this job",
                    HttpStatus.BAD_REQUEST);
        }

        JobApplication jobApplication = JobApplication.builder()
                .jobPosting(jobPosting)
                .applicant(currentUser)
                .coverLetter(request.getCoverLetter())
                .isAccepted(false)
                .appliedAt(LocalDateTime.now())
                .build();

        JobApplication savedApplication = jobApplicationDao.save(jobApplication);
        BaseResponse<JobApplication> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.CREATED,
                MessageType.CREATED,
                "Application submitted successfully",
                webRequest,
                savedApplication);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<JobApplication>> getApplicationById(UUID id) {
        Optional<JobApplication> applicationOptional = jobApplicationDao.findById(id);

        if (applicationOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Application not found",
                    HttpStatus.NOT_FOUND);
        }

        BaseResponse<JobApplication> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Application found",
                webRequest,
                applicationOptional.get());
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobApplication>>> getApplicationsByJobPosting(UUID jobPostingId,
            Pageable pageable) {
        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = jobPostingService.getJobPostingById(jobPostingId);
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingResponse.getBody().getData().get("jobPosting");
        Page<JobApplication> applications = jobApplicationDao.findByJobPosting(jobPosting, pageable);

        BaseResponse<Page<JobApplication>> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Applications retrieved successfully",
                webRequest,
                applications);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobApplication>>> getApplicationsByApplicant(UUID applicantId,
            Pageable pageable) {
        UserEmployee applicant = (UserEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<JobApplication> applications = jobApplicationDao.findByApplicant(applicant, pageable);

        BaseResponse<Page<JobApplication>> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Applications retrieved successfully",
                webRequest,
                applications);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobApplication>> acceptApplication(UUID applicationId) {
        ResponseEntity<BaseResponse<JobApplication>> applicationResponse = getApplicationById(applicationId);
        if (applicationResponse.getStatusCode() != HttpStatus.OK) {
            return applicationResponse;
        }

        JobApplication application = applicationResponse.getBody().getData().get("application");
        application.setAccepted(true);
        application.setRespondedAt(LocalDateTime.now());
        JobApplication savedApplication = jobApplicationDao.save(application);

        BaseResponse<JobApplication> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.UPDATED,
                "Application accepted successfully",
                webRequest,
                savedApplication);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobApplication>> rejectApplication(UUID applicationId, String reason) {
        ResponseEntity<BaseResponse<JobApplication>> applicationResponse = getApplicationById(applicationId);
        if (applicationResponse.getStatusCode() != HttpStatus.OK) {
            return applicationResponse;
        }

        JobApplication application = applicationResponse.getBody().getData().get("application");
        application.setAccepted(false);
        application.setRejectionReason(reason);
        application.setRespondedAt(LocalDateTime.now());
        JobApplication savedApplication = jobApplicationDao.save(application);

        BaseResponse<JobApplication> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.UPDATED,
                "Application rejected successfully",
                webRequest,
                savedApplication);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Void>> withdrawApplication(UUID applicationId) {
        ResponseEntity<BaseResponse<JobApplication>> applicationResponse = getApplicationById(applicationId);
        if (applicationResponse.getStatusCode() != HttpStatus.OK) {
            BaseResponse<Void> errorResponse = baseResponseHelpers.createBaseResponse(
                    applicationResponse.getBody().getHttpStatus(),
                    applicationResponse.getBody().getMessageType(),
                    applicationResponse.getBody().getMessage(),
                    webRequest,
                    null);
            return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse);
        }

        jobApplicationDao.deleteById(applicationId);

        BaseResponse<Void> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.DELETED,
                "Application withdrawn successfully",
                webRequest,
                null);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}