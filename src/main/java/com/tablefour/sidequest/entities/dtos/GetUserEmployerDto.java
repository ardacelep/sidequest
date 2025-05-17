package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.Rating;
import com.tablefour.sidequest.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserEmployerDto {

    private String id_card_number;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private boolean isRestricted;

    private Gender gender;

    private double rating;

    private Set<Rating> receivedRatings;

    private String companyName;
    private String companyDescription;

    private int givenJobs = 0;

}
