package com.tablefour.sidequest.dataAccess;

import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.UserEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationDao extends JpaRepository<JobApplication, UUID> {
    Page<JobApplication> findByJobPosting(JobPosting jobPosting, Pageable pageable);

    Page<JobApplication> findByApplicant(UserEmployee applicant, Pageable pageable);

    Optional<JobApplication> findByJobPostingAndApplicant(JobPosting jobPosting, UserEmployee applicant);

    List<JobApplication> findByJobPostingAndIsAcceptedTrue(JobPosting jobPosting);

    boolean existsByJobPostingAndApplicant(JobPosting jobPosting, UserEmployee applicant);
}