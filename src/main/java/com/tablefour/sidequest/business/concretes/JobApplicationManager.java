package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.JobApplicationService;
import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.dataAccess.JobApplicationDao;
import com.tablefour.sidequest.dataAccess.JobPostingDao;
import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.JobApplicationRequest;
import com.tablefour.sidequest.entities.enums.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobApplicationManager implements JobApplicationService {

    private final JobApplicationDao jobApplicationDao;
    private final JobPostingDao jobPostingDao;
    private final JobPostingService jobPostingService;
    private final BaseResponseHelpers baseResponseHelpers;
    private final WebRequest webRequest;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobApplication>> applyForJob(JobApplicationRequest request) {
        UserEmployee currentUser = (UserEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Optional<JobPosting> jobPostingOptional = jobPostingService.getJobPostingEntityById(request.getJobPostingId());
        if (jobPostingOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingOptional.get();

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
        Optional<JobApplication> applicationOptional = jobApplicationDao.findByIdWithJobPosting(id);

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
    public ResponseEntity<PageResponse<JobApplication>> getApplicationsByJobPosting(UUID jobPostingId,
            Pageable pageable) {
        Optional<JobPosting> jobPostingOptional = jobPostingService.getJobPostingEntityById(jobPostingId);
        if (jobPostingOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        Page<JobApplication> applications = jobApplicationDao.findByJobPosting(jobPostingOptional.get(), pageable);

        PageResponse<JobApplication> response = PageResponse.of(
                applications,
                HttpStatus.OK,
                MessageType.FOUND,
                "Applications retrieved successfully",
                baseResponseHelpers.getHostName(),
                webRequest.getDescription(false).substring(4),
                webRequest instanceof ServletWebRequest ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                        : "GET");

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<PageResponse<JobApplication>> getApplicationsByApplicant(UUID applicantId,
            Pageable pageable) {
        UserEmployee applicant = (UserEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Page<JobApplication> applications = jobApplicationDao.findByApplicant(applicant, pageable);

        PageResponse<JobApplication> response = PageResponse.of(
                applications,
                HttpStatus.OK,
                MessageType.FOUND,
                "Applications retrieved successfully",
                baseResponseHelpers.getHostName(),
                webRequest.getDescription(false).substring(4),
                webRequest instanceof ServletWebRequest ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                        : "GET");

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    private void verifyApplicantOwnership(JobApplication application) {
        UserEmployee currentUser = (UserEmployee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!application.getApplicant().getId().equals(currentUser.getId())) {
            throw new RuntimeBaseException(
                    ErrorMessageType.ERROR,
                    "You are not authorized to modify this application",
                    HttpStatus.FORBIDDEN);
        }
    }

    private void verifyEmployerOwnership(JobApplication application) {
        UserEmployer currentUser = (UserEmployer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!application.getJobPosting().getEmployer().getId().equals(currentUser.getId())) {
            throw new RuntimeBaseException(
                    ErrorMessageType.ERROR,
                    "You are not authorized to modify this application",
                    HttpStatus.FORBIDDEN);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobApplication>> acceptApplication(UUID id) {
        Optional<JobApplication> applicationOptional = jobApplicationDao.findByIdWithJobPosting(id);
        if (applicationOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Application not found",
                    HttpStatus.NOT_FOUND);
        }

        JobApplication application = applicationOptional.get();
        verifyEmployerOwnership(application);

        // Update the job posting to assign the employee
        JobPosting jobPosting = application.getJobPosting();
        jobPosting.setAssignedEmployee(application.getApplicant());
        jobPosting.setStatus(JobStatus.FILLED);
        jobPostingDao.save(jobPosting);

        // Update the application
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
    public ResponseEntity<BaseResponse<JobApplication>> rejectApplication(UUID id, String reason) {
        Optional<JobApplication> applicationOptional = jobApplicationDao.findById(id);
        if (applicationOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Application not found",
                    HttpStatus.NOT_FOUND);
        }

        JobApplication application = applicationOptional.get();
        verifyEmployerOwnership(application);
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
    public ResponseEntity<BaseResponse<Void>> withdrawApplication(UUID id) {
        Optional<JobApplication> applicationOptional = jobApplicationDao.findById(id);
        if (applicationOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Application not found",
                    HttpStatus.NOT_FOUND);
        }

        JobApplication application = applicationOptional.get();
        verifyApplicantOwnership(application);
        jobApplicationDao.deleteById(id);

        BaseResponse<Void> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.DELETED,
                "Application withdrawn successfully",
                webRequest,
                null);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}