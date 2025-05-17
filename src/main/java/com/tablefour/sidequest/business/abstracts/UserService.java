package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.BaseResponse;
import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.core.results.Result;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    DataResult<User> getUserById(String userId);

    DataResult<User> getUserByEmail(String email);

    DataResult<User> getUserByPhoneNumber(String phoneNumber);

    Result addUser(User user);

    DataResult<User> getAuthenticatedUser();

    ResponseEntity<PageResponse<UserEmployee>> getAllEmployees(Pageable pageable);

    ResponseEntity<PageResponse<UserEmployer>> getAllEmployers(Pageable pageable);

    ResponseEntity<PageResponse<User>> getAllUsers(Pageable pageable);
}
