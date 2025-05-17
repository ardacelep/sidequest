package com.tablefour.sidequest.controllers;

import com.tablefour.sidequest.business.abstracts.UserService;
import com.tablefour.sidequest.core.results.PageResponse;
import com.tablefour.sidequest.entities.User;
import com.tablefour.sidequest.entities.UserEmployee;
import com.tablefour.sidequest.entities.UserEmployer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/employees")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserEmployee>> getAllEmployees(Pageable pageable) {
        return userService.getAllEmployees(pageable);
    }

    @GetMapping("/employers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserEmployer>> getAllEmployers(Pageable pageable) {
        return userService.getAllEmployers(pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<User>> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }
}