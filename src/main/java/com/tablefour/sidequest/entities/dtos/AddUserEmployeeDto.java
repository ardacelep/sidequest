package com.tablefour.sidequest.entities.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserEmployeeDto {
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String email;
    private String university;
    private LocalDate birthDate;
}
