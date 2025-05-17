package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.dataAccess.JobPostingDao;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.CreateJobPostingRequest;
import com.tablefour.sidequest.entities.enums.JobCategory;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobPostingManager implements JobPostingService {

    private final JobPostingDao jobPostingDao;
    private final UserService userService;
    private final BaseResponseHelpers baseResponseHelpers;
    private final WebRequest webRequest;

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobPosting>> createJobPosting(CreateJobPostingRequest request) {
        UserEmployer currentUser = (UserEmployer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        JobPosting jobPosting = JobPosting.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .payment(request.getPayment())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(request.getCategory())
                .requiredEmployeeCount(request.getRequiredEmployeeCount())
                .isUrgent(request.isUrgent())
                .requirements(request.getRequirements())
                .employer(currentUser)
                .status(JobStatus.OPEN)
                .isActive(true)
                .build();

        JobPosting savedJobPosting = jobPostingDao.save(jobPosting);
        BaseResponse<JobPosting> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.CREATED,
                MessageType.CREATED,
                "Job posting created successfully",
                webRequest,
                savedJobPosting);

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<JobPosting>> getJobPostingById(UUID id) {
        Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(id);

        if (jobPostingOptional.isEmpty()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        BaseResponse<JobPosting> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Job posting found",
                webRequest,
                jobPostingOptional.get());
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getAllActiveJobPostings(Pageable pageable) {
        Page<JobPosting> activeJobPostings = jobPostingDao.findByIsActiveTrue(pageable);
        BaseResponse<Page<JobPosting>> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Active job postings retrieved",
                webRequest,
                activeJobPostings);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByCategory(JobCategory category,
            Pageable pageable) {
        Page<JobPosting> jobPostings = jobPostingDao.findByCategory(category, pageable);
        BaseResponse<Page<JobPosting>> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Job postings by category retrieved",
                webRequest,
                jobPostings);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobPosting>>> getJobPostingsByEmployer(UUID employerId, Pageable pageable) {
        DataResult<User> employerResult = userService.getUserById(employerId.toString());

        if (!employerResult.isSuccess()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Employer not found",
                    HttpStatus.NOT_FOUND);
        }

        UserEmployer employer = (UserEmployer) employerResult.getData();
        Page<JobPosting> jobPostings = jobPostingDao.findByEmployer(employer, pageable);

        BaseResponse<Page<JobPosting>> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.FOUND,
                "Employer's job postings retrieved",
                webRequest,
                jobPostings);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobPosting>> updateJobStatus(UUID id, JobStatus newStatus) {
        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = getJobPostingById(id);
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingResponse.getBody().getData().get("jobPosting");
        jobPosting.setStatus(newStatus);
        JobPosting updatedPosting = jobPostingDao.save(jobPosting);

        BaseResponse<JobPosting> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.UPDATED,
                "Job status updated successfully",
                webRequest,
                updatedPosting);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobPosting>> deactivateJobPosting(UUID id) {
        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = getJobPostingById(id);
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingResponse.getBody().getData().get("jobPosting");
        jobPosting.setActive(false);
        JobPosting deactivatedPosting = jobPostingDao.save(jobPosting);

        BaseResponse<JobPosting> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.UPDATED,
                "Job posting deactivated successfully",
                webRequest,
                deactivatedPosting);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<BaseResponse<Page<JobPosting>>> searchJobPostings(String location, Double minPayment,
            JobCategory category, Boolean isUrgent, Pageable pageable) {
        // For simplicity, we're just using location search. In a real application,
        // you'd want to implement a more sophisticated search using criteria or
        // specifications
        if (location != null) {
            Page<JobPosting> searchResults = jobPostingDao.findByLocationContainingIgnoreCase(location, pageable);
            BaseResponse<Page<JobPosting>> response = baseResponseHelpers.createBaseResponse(
                    HttpStatus.OK,
                    MessageType.FOUND,
                    "Search results retrieved",
                    webRequest,
                    searchResults);
            return ResponseEntity.status(response.getHttpStatus()).body(response);
        }
        return getAllActiveJobPostings(pageable);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<Void>> deleteJobPosting(UUID id) {
        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = getJobPostingById(id);
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        jobPostingDao.deleteById(id);

        BaseResponse<Void> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.DELETED,
                "Job posting deleted successfully",
                webRequest,
                null);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    @Transactional
    public ResponseEntity<BaseResponse<JobPosting>> assignEmployee(UUID jobId, UUID employeeId) {
        ResponseEntity<BaseResponse<JobPosting>> jobPostingResponse = getJobPostingById(jobId);
        if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Job posting not found",
                    HttpStatus.NOT_FOUND);
        }

        JobPosting jobPosting = jobPostingResponse.getBody().getData().get("jobPosting");
        DataResult<User> employeeResult = userService.getUserById(employeeId.toString());

        if (!employeeResult.isSuccess()) {
            throw new RuntimeBaseException(
                    ErrorMessageType.NO_RECORD_EXISTS,
                    "Employee not found",
                    HttpStatus.NOT_FOUND);
        }

        UserEmployee employee = (UserEmployee) employeeResult.getData();
        jobPosting.setAssignedEmployee(employee);
        jobPosting.setStatus(JobStatus.FILLED);
        JobPosting savedPosting = jobPostingDao.save(jobPosting);

        BaseResponse<JobPosting> response = baseResponseHelpers.createBaseResponse(
                HttpStatus.OK,
                MessageType.UPDATED,
                "Employee assigned successfully",
                webRequest,
                savedPosting);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}