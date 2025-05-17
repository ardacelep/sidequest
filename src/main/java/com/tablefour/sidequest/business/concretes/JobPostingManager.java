package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.JobPostingService;
import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.mappers.JobPostingMapper;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.dataAccess.JobPostingDao;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.CreateJobPostingRequest;
import com.tablefour.sidequest.entities.dtos.JobPostingDateSearchRequest;
import com.tablefour.sidequest.entities.dtos.JobPostingDto;
import com.tablefour.sidequest.entities.enums.DateFieldType;
import com.tablefour.sidequest.entities.enums.DateSearchType;
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
import org.springframework.web.context.request.ServletWebRequest;

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
        public ResponseEntity<BaseResponse<JobPostingDto>> createJobPosting(CreateJobPostingRequest request) {
                UserEmployer currentUser = (UserEmployer) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();

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
                JobPostingDto dto = JobPostingMapper.toDto(savedJobPosting);

                BaseResponse<JobPostingDto> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.CREATED,
                                MessageType.CREATED,
                                "Job posting created successfully",
                                webRequest,
                                dto);

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<BaseResponse<JobPostingDto>> getJobPostingById(UUID id) {
                Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(id);

                if (jobPostingOptional.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                JobPostingDto dto = JobPostingMapper.toDto(jobPostingOptional.get());
                BaseResponse<JobPostingDto> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Job posting found",
                                webRequest,
                                dto);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> getAllActiveJobPostings(Pageable pageable) {
                Page<JobPosting> activeJobPostings = jobPostingDao.findByIsActiveTrue(pageable);
                Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(activeJobPostings);

                PageResponse<JobPostingDto> response = PageResponse.of(
                                dtoPage,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Active job postings retrieved",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> getJobPostingsByCategory(JobCategory category,
                        Pageable pageable) {
                Page<JobPosting> jobPostings = jobPostingDao.findByCategory(category, pageable);
                Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(jobPostings);

                PageResponse<JobPostingDto> response = PageResponse.of(
                                dtoPage,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Job postings by category retrieved",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> getJobPostingsByEmployer(UUID employerId,
                        Pageable pageable) {
                DataResult<User> employerResult = userService.getUserById(employerId.toString());

                if (!employerResult.isSuccess()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Employer not found",
                                        HttpStatus.NOT_FOUND);
                }

                UserEmployer employer = (UserEmployer) employerResult.getData();
                Page<JobPosting> jobPostings = jobPostingDao.findByEmployer(employer, pageable);
                Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(jobPostings);

                PageResponse<JobPostingDto> response = PageResponse.of(
                                dtoPage,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Employer's job postings retrieved",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        private void verifyJobPostingOwnership(JobPosting jobPosting) {
                UserEmployer currentUser = (UserEmployer) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                if (!jobPosting.getEmployer().getId().equals(currentUser.getId())) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.ERROR,
                                        "You are not authorized to modify this job posting",
                                        HttpStatus.FORBIDDEN);
                }
        }

        @Override
        @Transactional
        public ResponseEntity<BaseResponse<JobPostingDto>> updateJobStatus(UUID id, JobStatus newStatus) {
                Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(id);
                if (jobPostingOptional.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                JobPosting jobPosting = jobPostingOptional.get();
                verifyJobPostingOwnership(jobPosting);
                jobPosting.setStatus(newStatus);
                JobPosting updatedPosting = jobPostingDao.save(jobPosting);
                JobPostingDto dto = JobPostingMapper.toDto(updatedPosting);

                BaseResponse<JobPostingDto> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.UPDATED,
                                "Job status updated successfully",
                                webRequest,
                                dto);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        @Transactional
        public ResponseEntity<BaseResponse<JobPostingDto>> deactivateJobPosting(UUID id) {
                Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(id);
                if (jobPostingOptional.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                JobPosting jobPosting = jobPostingOptional.get();
                verifyJobPostingOwnership(jobPosting);
                jobPosting.setActive(false);
                JobPosting deactivatedPosting = jobPostingDao.save(jobPosting);
                JobPostingDto dto = JobPostingMapper.toDto(deactivatedPosting);

                BaseResponse<JobPostingDto> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.UPDATED,
                                "Job posting deactivated successfully",
                                webRequest,
                                dto);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        @Transactional
        public ResponseEntity<BaseResponse<Void>> deleteJobPosting(UUID id) {
                Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(id);
                if (jobPostingOptional.isEmpty()) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                JobPosting jobPosting = jobPostingOptional.get();
                verifyJobPostingOwnership(jobPosting);
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
        public ResponseEntity<BaseResponse<JobPostingDto>> assignEmployee(UUID jobId, UUID employeeId) {
                ResponseEntity<BaseResponse<JobPostingDto>> jobPostingResponse = getJobPostingById(jobId);
                if (jobPostingResponse.getStatusCode() != HttpStatus.OK) {
                        throw new RuntimeBaseException(
                                        ErrorMessageType.NO_RECORD_EXISTS,
                                        "Job posting not found",
                                        HttpStatus.NOT_FOUND);
                }

                Optional<JobPosting> jobPostingOptional = jobPostingDao.findById(jobId);
                JobPosting jobPosting = jobPostingOptional.get();
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
                JobPostingDto dto = JobPostingMapper.toDto(savedPosting);

                BaseResponse<JobPostingDto> response = baseResponseHelpers.createBaseResponse(
                                HttpStatus.OK,
                                MessageType.UPDATED,
                                "Employee assigned successfully",
                                webRequest,
                                dto);
                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> searchJobPostingsByKeyword(String keyword,
                        Pageable pageable) {
                if (keyword == null || keyword.trim().isEmpty()) {
                        return getAllActiveJobPostings(pageable);
                }

                Page<JobPosting> searchResults = jobPostingDao
                                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                                                keyword.trim(), keyword.trim(), pageable);
                Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(searchResults);

                PageResponse<JobPostingDto> response = PageResponse.of(
                                dtoPage,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Search results retrieved successfully",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> searchJobPostingsByDate(JobPostingDateSearchRequest request,
                        Pageable pageable) {
                Page<JobPosting> searchResults;
                LocalDateTime date = request.getDate();

                if (request.getFieldType() == DateFieldType.START_DATE) {
                        switch (request.getSearchType()) {
                                case EXACT -> searchResults = jobPostingDao.findByStartDate(date, pageable);
                                case BEFORE -> searchResults = jobPostingDao.findByStartDateBefore(date, pageable);
                                case AFTER -> searchResults = jobPostingDao.findByStartDateAfter(date, pageable);
                                default -> throw new RuntimeBaseException(
                                                ErrorMessageType.INVALID_REQUEST,
                                                "Invalid date search type",
                                                HttpStatus.BAD_REQUEST);
                        }
                } else {
                        switch (request.getSearchType()) {
                                case EXACT -> searchResults = jobPostingDao.findByEndDate(date, pageable);
                                case BEFORE -> searchResults = jobPostingDao.findByEndDateBefore(date, pageable);
                                case AFTER -> searchResults = jobPostingDao.findByEndDateAfter(date, pageable);
                                default -> throw new RuntimeBaseException(
                                                ErrorMessageType.INVALID_REQUEST,
                                                "Invalid date search type",
                                                HttpStatus.BAD_REQUEST);
                        }
                }

                Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(searchResults);

                PageResponse<JobPostingDto> response = PageResponse.of(
                                dtoPage,
                                HttpStatus.OK,
                                MessageType.FOUND,
                                "Date-based search results retrieved successfully",
                                baseResponseHelpers.getHostName(),
                                webRequest.getDescription(false).substring(4),
                                webRequest instanceof ServletWebRequest
                                                ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                : "GET");

                return ResponseEntity.status(response.getHttpStatus()).body(response);
        }

        @Override
        public Optional<JobPosting> getJobPostingEntityById(UUID id) {
                return jobPostingDao.findById(id);
        }

        @Override
        public ResponseEntity<PageResponse<JobPostingDto>> searchJobPostings(String location, Double minPayment,
                        JobCategory category, Boolean isUrgent, Pageable pageable) {
                if (location != null) {
                        Page<JobPosting> searchResults = jobPostingDao.findByLocationContainingIgnoreCase(location,
                                        pageable);
                        Page<JobPostingDto> dtoPage = JobPostingMapper.toDtoPage(searchResults);

                        PageResponse<JobPostingDto> response = PageResponse.of(
                                        dtoPage,
                                        HttpStatus.OK,
                                        MessageType.FOUND,
                                        "Search results retrieved",
                                        baseResponseHelpers.getHostName(),
                                        webRequest.getDescription(false).substring(4),
                                        webRequest instanceof ServletWebRequest
                                                        ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                                                        : "GET");

                        return ResponseEntity.status(response.getHttpStatus()).body(response);
                }
                return getAllActiveJobPostings(pageable);
        }
}