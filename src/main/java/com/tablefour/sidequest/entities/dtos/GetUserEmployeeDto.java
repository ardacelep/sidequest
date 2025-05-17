package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.enums.Gender;
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
    private double rating;
    private int completedJobs;

    private Gender gender;

    private Set<Role> authorities;

    private boolean isRestricted;

    private Set<Rating> receivedRatings;
}
