package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.business.constants.Messages;
import com.tablefour.sidequest.core.results.*;
import com.tablefour.sidequest.dataAccess.UserDao;
import com.tablefour.sidequest.dataAccess.UserEmployeeDao;
import com.tablefour.sidequest.dataAccess.UserEmployerDao;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import com.tablefour.sidequest.entities.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserManager implements UserService {

    private final UserDao userDao;
    private final UserEmployeeDao userEmployeeDao;
    private final UserEmployerDao userEmployerDao;
    private final BaseResponseHelpers baseResponseHelpers;
    private final WebRequest webRequest;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return getUserByEmail(username).getData();
    }

    @Override
    public DataResult<User> getUserById(String userId) {
        var result = userDao.findById(UUID.fromString(userId));

        if (result.isEmpty()) {
            return new ErrorDataResult<>(Messages.userNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<User>(result.get(), Messages.userFound, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserByEmail(String email) {
        var result = userDao.findByEmail(email);

        if (result.isEmpty()) {
            return new ErrorDataResult<>(Messages.userNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<User>(result.get(), Messages.userFound, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserByPhoneNumber(String phoneNumber) {
        var result = userDao.findByPhoneNumber(phoneNumber);

        if (result.isEmpty()) {
            return new ErrorDataResult<>(Messages.userNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<User>(result.get(), Messages.userFound, HttpStatus.OK);
    }

    @Override
    public Result addUser(User user) {
        user.setAuthorities(Set.of(Role.ROLE_USER));
        userDao.save(user);
        return new SuccessDataResult<User>(user, Messages.userAdded, HttpStatus.CREATED);
    }

    @Override
    public DataResult<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userMail = authentication.getName();
        return getUserByEmail(userMail);
    }

    @Override
    public ResponseEntity<PageResponse<UserEmployee>> getAllEmployees(Pageable pageable) {
        Page<UserEmployee> employees = userEmployeeDao.findAll(pageable);

        PageResponse<UserEmployee> response = PageResponse.of(
                employees,
                HttpStatus.OK,
                MessageType.FOUND,
                "All employees retrieved successfully",
                baseResponseHelpers.getHostName(),
                webRequest.getDescription(false).substring(4),
                webRequest instanceof ServletWebRequest ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                        : "GET");

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<PageResponse<UserEmployer>> getAllEmployers(Pageable pageable) {
        Page<UserEmployer> employers = userEmployerDao.findAll(pageable);

        PageResponse<UserEmployer> response = PageResponse.of(
                employers,
                HttpStatus.OK,
                MessageType.FOUND,
                "All employers retrieved successfully",
                baseResponseHelpers.getHostName(),
                webRequest.getDescription(false).substring(4),
                webRequest instanceof ServletWebRequest ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                        : "GET");

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }

    @Override
    public ResponseEntity<PageResponse<User>> getAllUsers(Pageable pageable) {
        Page<User> users = userDao.findAll(pageable);

        PageResponse<User> response = PageResponse.of(
                users,
                HttpStatus.OK,
                MessageType.FOUND,
                "All users retrieved successfully",
                baseResponseHelpers.getHostName(),
                webRequest.getDescription(false).substring(4),
                webRequest instanceof ServletWebRequest ? ((ServletWebRequest) webRequest).getRequest().getMethod()
                        : "GET");

        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
