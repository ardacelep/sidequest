package com.tablefour.sidequest.dataAccess;

import com.tablefour.sidequest.entities.UserEmployer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserEmployerDao extends JpaRepository<UserEmployer, UUID> {
}
