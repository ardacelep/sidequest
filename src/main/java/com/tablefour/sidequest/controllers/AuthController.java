package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.core.exception.ErrorMessageType;
import com.tablefour.sidequest.core.exception.RuntimeBaseException;
import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.*;
import com.tablefour.sidequest.entities.enums.Gender;
import com.tablefour.sidequest.entities.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tablefour.sidequest.business.abstracts.AuthService;
import org.springframework.web.context.request.WebRequest;

import java.text.MessageFormat;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final BaseResponseHelpers baseResponseHelpers;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        var user = User.builder()
                .email(loginDto.getEmail())
                .password(loginDto.getPassword())
                .build();

        var result = authService.login(user);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto){

        var user = User.builder()
                .email(registerDto.getEmail())
                .password(registerDto.getPassword())
                .build();

        var result = authService.register(user);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/register/user-employee")
    public ResponseEntity<BaseResponse<GetUserEmployeeDto>> registerUserEmployee(@RequestBody @Valid AddUserEmployeeDto addUserEmployeeDto, WebRequest webRequest) {

        UserEmployee userEmployee = new UserEmployee();

        BeanUtils.copyProperties(addUserEmployeeDto, userEmployee);

        if (addUserEmployeeDto.getGender().equalsIgnoreCase("male")){
            userEmployee.setGender(Gender.MALE);
        }
        else if (addUserEmployeeDto.getGender().equalsIgnoreCase("female")) {
            userEmployee.setGender(Gender.FEMALE);
        }
        else {
            throw new RuntimeBaseException(ErrorMessageType.INVALID_CREDENTIALS, MessageFormat.format("no valid gender was given: {0}", addUserEmployeeDto.getGender()),HttpStatus.BAD_REQUEST);
        }

        GetUserEmployeeDto responseData = authService.registerUserEmployee(userEmployee);
        BaseResponse<GetUserEmployeeDto> responseBody = baseResponseHelpers.createBaseResponse(HttpStatus.CREATED, MessageType.CREATED, "Employee was successfully saved.", webRequest, responseData);

        return ResponseEntity.status(responseBody.getHttpStatus()).body(responseBody);

    }

    @PostMapping("/register/user-employer")
    ResponseEntity<BaseResponse<GetUserEmployerDto>> registerUserEmployer(@RequestBody @Valid AddUserEmployerDto addUserEmployerDto, WebRequest webRequest) {

        UserEmployer userEmployer = new UserEmployer();

        BeanUtils.copyProperties(addUserEmployerDto, userEmployer);

        if (addUserEmployerDto.getGender().equalsIgnoreCase("male")){
            userEmployer.setGender(Gender.MALE);
        }
        else if (addUserEmployerDto.getGender().equalsIgnoreCase("female")) {
            userEmployer.setGender(Gender.FEMALE);
        }
        else {
            throw new RuntimeBaseException(ErrorMessageType.INVALID_CREDENTIALS, MessageFormat.format("no valid gender was given: {0}", addUserEmployerDto.getGender()),HttpStatus.BAD_REQUEST);
        }

        GetUserEmployerDto responseData = authService.registerUserEmployer(userEmployer);
        BaseResponse<GetUserEmployerDto> responseBody = baseResponseHelpers.createBaseResponse(HttpStatus.CREATED, MessageType.CREATED, "Employer was successfully saved.", webRequest, responseData);

        return ResponseEntity.status(responseBody.getHttpStatus()).body(responseBody);

    }

}
