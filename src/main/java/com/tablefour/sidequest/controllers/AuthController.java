package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.BaseResponseHelpers;
import com.tablefour.sidequest.core.results.MessageType;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.dtos.AddUserEmployeeDto;
import com.tablefour.sidequest.entities.dtos.GetUserEmployeeDto;
import com.tablefour.sidequest.entities.dtos.LoginDto;
import com.tablefour.sidequest.entities.dtos.RegisterDto;
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

    @PostMapping("/register/userEmployee")
    public ResponseEntity<BaseResponse<GetUserEmployeeDto>> registerUserEmployee(@RequestBody @Valid AddUserEmployeeDto addUserEmployeeDto, WebRequest webRequest) {

        UserEmployee userEmployee = new UserEmployee();

        BeanUtils.copyProperties(addUserEmployeeDto, userEmployee);

        GetUserEmployeeDto responseData = authService.registerUserEmployee(userEmployee);
        BaseResponse<GetUserEmployeeDto> responseBody = baseResponseHelpers.createBaseResponse(HttpStatus.CREATED, MessageType.CREATED, "Employee was successfully saved.", webRequest, responseData);

        return ResponseEntity.status(responseBody.getHttpStatus()).body(responseBody);

    }

}
