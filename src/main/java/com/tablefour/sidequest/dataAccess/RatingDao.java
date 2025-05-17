package com.tablefour.sidequest.dataAccess;

import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RatingDao extends JpaRepository<Rating, UUID> {
    Page<Rating> findByRatedUser(User ratedUser, Pageable pageable);

    Page<Rating> findByRaterUser(User raterUser, Pageable pageable);

    @Query("SELECT AVG(r.value) FROM Rating r WHERE r.ratedUser.id = :userId")
    Double getAverageRatingForUser(@Param("userId") UUID userId);

    boolean existsByRaterUserAndJobPosting_Id(User raterUser, UUID jobPostingId);
}