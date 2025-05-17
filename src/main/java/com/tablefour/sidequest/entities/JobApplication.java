package com.tablefour.sidequest.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "job_applications")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private UserEmployee applicant;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private boolean isAccepted;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    private LocalDateTime respondedAt;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
}