package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.enums.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GetUserEmployeeDto {

    private UUID id;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String university;
    private LocalDate birthDate;
    private float rating;
    private int completedJobs;

    private Set<Role> authorities;

    private boolean isRestricted;
}
