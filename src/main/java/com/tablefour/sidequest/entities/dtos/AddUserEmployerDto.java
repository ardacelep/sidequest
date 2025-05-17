package com.tablefour.sidequest.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserEmployerDto {

    private String id_card_number;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String email;
    private String companyName;
    private String companyDescription;
    private String gender;


}
