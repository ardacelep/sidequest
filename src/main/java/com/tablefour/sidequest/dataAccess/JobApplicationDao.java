package com.tablefour.sidequest.dataAccess;

import com.tablefour.sidequest.entities.JobApplication;
import com.tablefour.sidequest.entities.JobPosting;
import com.tablefour.sidequest.entities.UserEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT CASE WHEN COUNT(ja) > 0 THEN true ELSE false END FROM JobApplication ja " +
            "WHERE ja.jobPosting = :jobPosting AND ja.applicant = :applicant")
    boolean existsByJobPostingAndApplicant(@Param("jobPosting") JobPosting jobPosting,
            @Param("applicant") UserEmployee applicant);

    @Query("SELECT ja FROM JobApplication ja " +
            "LEFT JOIN FETCH ja.jobPosting jp " +
            "LEFT JOIN FETCH jp.employer " +
            "LEFT JOIN FETCH ja.applicant " +
            "WHERE ja.id = :id")
    Optional<JobApplication> findByIdWithJobPosting(@Param("id") UUID id);
}