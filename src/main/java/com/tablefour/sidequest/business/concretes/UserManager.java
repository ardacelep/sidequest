package com.tablefour.sidequest.business.concretes;

import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.business.constants.Messages;
import com.tablefour.sidequest.core.results.DataResult;
import com.tablefour.sidequest.core.results.ErrorDataResult;
import com.tablefour.sidequest.core.results.Result;
import com.tablefour.sidequest.core.results.SuccessDataResult;
import com.tablefour.sidequest.dataAccess.UserDao;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.enums.Role;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;

    public UserManager(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        return getUserByEmail(username).getData();
    }

    @Override
    public DataResult<User> getUserById(String userId) {
        var result = userDao.findById(UUID.fromString(userId));

        if (result.isEmpty()){
            return new ErrorDataResult<>(Messages.userNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<User>(result.get(), Messages.userFound, HttpStatus.OK);

    }

    @Override
    public DataResult<User> getUserByEmail(String email) {
        var result = userDao.findByEmail(email);

        if (result.isEmpty()){
            return new ErrorDataResult<>(Messages.userNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<User>(result.get(), Messages.userFound, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserByPhoneNumber(String phoneNumber) {

        var result = userDao.findByPhoneNumber(phoneNumber);

        if (result.isEmpty()){
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


}
