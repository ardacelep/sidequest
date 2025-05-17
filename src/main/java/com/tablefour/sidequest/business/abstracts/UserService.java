package com.tablefour.sidequest.business.abstracts;

import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.Result;
import com.tablefour.sidequest.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.UUID;

public interface UserService extends UserDetailsService {

    DataResult<User> getUserById(String userId);

    DataResult<User> getUserByEmail(String email);

    DataResult<User> getUserByPhoneNumber(String phoneNumber);

    Result addUser(User user);

    DataResult<User> getAuthenticatedUser();

}
