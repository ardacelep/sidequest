package com.tablefour.sidequest.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tablefour.sidequest.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT COALESCE(AVG(r.value), 0.0) FROM Rating r WHERE r.ratedUser.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") UUID userId);

}
