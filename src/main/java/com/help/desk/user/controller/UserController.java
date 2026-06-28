package com.help.desk.user.controller;

import com.help.desk.exception.ApiResponse;
import com.help.desk.exception.TooManyRequestsException;
import com.help.desk.radis.service.RateLimiterService;
import com.help.desk.user.dto.request.CreateUserRequest;
import com.help.desk.user.dto.request.UpdateRoleRequest;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.enums.UserRole;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import com.help.desk.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    private RateLimiterService rateLimiterService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        String key = "Register";
        if(!rateLimiterService.isAllowed(key,10,60)){
            throw new TooManyRequestsException("Too many requests. Please try again after 1 minute.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('TEAM_LEAD') or hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                userService.getAllUsers(
                        page,
                        size,
                        sortBy,
                        sortDirection));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('TEAM_LEAD') or hasRole('MANAGER')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @PathVariable UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('TEAM_LEAD') or hasRole('MANAGER')")
    @GetMapping("/status/{active}")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(
            @PathVariable Boolean active) {
        return ResponseEntity.ok(userService.getUsersByActive(active));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') ")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(
            @PathVariable Long id) {
        userService.activeUser(id);
        return ResponseEntity.ok("User activated successfully");
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long id) {
        userService.deactiveUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest role) {
        return ResponseEntity.ok(userService.updateRole(id, role));
    }

}