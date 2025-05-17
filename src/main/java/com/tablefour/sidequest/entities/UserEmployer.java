package com.tablefour.sidequest.entities;

import com.tablefour.sidequest.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
@Table(name = "user_employers")
public class UserEmployer extends User {

    private String companyName;

    @Column(columnDefinition = "TEXT")
    private String companyDescription;

    private int givenJobs = 0;

    @PrePersist
    public void assignEmployerRole() {
        setAuthorities(Set.of(Role.ROLE_EMPLOYER));
    }
}
