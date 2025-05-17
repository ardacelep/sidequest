package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.AuthService;
import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.results.*;
import com.tablefour.sidequest.core.security.JwtService;
import com.tablefour.sidequest.dataAccess.UserDao;
import com.tablefour.sidequest.dataAccess.UserEmployeeDao;
import com.tablefour.sidequest.dataAccess.UserEmployerDao;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.dtos.GetUserEmployeeDto;
import com.tablefour.sidequest.entities.dtos.GetUserEmployerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.business.constants.Messages;

@Service
@RequiredArgsConstructor
public class AuthManager implements AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final UserDao userDao;

    private final UserEmployeeDao userEmployeeDao;

    private final UserEmployerDao userEmployerDao;

    @Override
    public Result register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.addUser(user);
    }

    @Override
    public DataResult<String> login(User user) {
        if(user.getEmail() == null){
            return new ErrorDataResult<>(Messages.emailCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if(user.getPassword() == null){
            return new ErrorDataResult<>(Messages.passwordCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var userResult = userService.getUserByEmail(user.getEmail());

        if(!userResult.isSuccess()){
            return new ErrorDataResult<>(userResult.getMessage(), userResult.getHttpStatus());
        }


        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
        if (authentication.isAuthenticated()) {
            var token = jwtService.generateToken(user.getEmail(), user.getAuthorities());

            return new SuccessDataResult<>(token, Messages.tokenGeneratedSuccessfully, HttpStatus.CREATED);
        }

        return new ErrorDataResult<>(Messages.invalidUsernameOrPassword, HttpStatus.BAD_REQUEST);
    }

    @Override
    public GetUserEmployeeDto registerUserEmployee(UserEmployee userEmployee) {

        GetUserEmployeeDto getUserEmployeeDto = new GetUserEmployeeDto();

        userEmployee.setPassword(passwordEncoder.encode(userEmployee.getPassword()));

        userEmployeeDao.save(userEmployee);

        BeanUtils.copyProperties(userEmployee, getUserEmployeeDto);

        return getUserEmployeeDto;
    }

    @Override
    public GetUserEmployerDto registerUserEmployer(UserEmployer userEmployer) {

        GetUserEmployerDto getUserEmployerDto = new GetUserEmployerDto();
        userEmployer.setPassword(passwordEncoder.encode(userEmployer.getPassword()));
        userEmployerDao.save(userEmployer);
        BeanUtils.copyProperties(userEmployer, getUserEmployerDto);
        return getUserEmployerDto;
    }
}
