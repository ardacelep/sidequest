package com.tablefour.sidequest.entities;

import com.tablefour.sidequest.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
@Table(name = "user_employees")
public class UserEmployee extends User {

    private String university;
    private LocalDate birthDate;

    private int completedJobs = 0;

    @PrePersist
    public void assignEmployeeRole() {
        setAuthorities(Set.of(Role.ROLE_EMPLOYEE));
    }
}
