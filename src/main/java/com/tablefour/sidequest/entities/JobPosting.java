package com.tablefour.sidequest.entities;

import com.tablefour.sidequest.entities.enums.JobCategory;
import com.tablefour.sidequest.entities.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private UserEmployer employer;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private double payment;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private double aiRecommendation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory category;

    // Additional useful fields
    private int requiredEmployeeCount;

    private boolean isUrgent;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL)
    private Set<JobApplication> applications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_employee_id")
    private UserEmployee assignedEmployee;

    private LocalDateTime lastModifiedAt;

    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        status = JobStatus.OPEN;
        isActive = true;
        lastModifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedAt = LocalDateTime.now();
    }
}