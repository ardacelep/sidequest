package com.tablefour.sidequest.dataAccess;

import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobPostingDao extends JpaRepository<JobPosting, UUID> {
    Page<JobPosting> findByIsActiveTrue(Pageable pageable);

    Page<JobPosting> findByEmployer(UserEmployer employer, Pageable pageable);

    Page<JobPosting> findByCategory(JobCategory category, Pageable pageable);

    Page<JobPosting> findByStatus(JobStatus status, Pageable pageable);

    List<JobPosting> findByEndDateBeforeAndStatus(LocalDateTime date, JobStatus status);

    Page<JobPosting> findByIsUrgentTrueAndIsActiveTrue(Pageable pageable);

    Page<JobPosting> findByPaymentGreaterThanEqual(double minPayment, Pageable pageable);

    Page<JobPosting> findByLocationContainingIgnoreCase(String location, Pageable pageable);
}