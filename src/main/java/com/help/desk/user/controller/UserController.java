package com.help.desk.user.controller;

import com.help.desk.exception.ApiResponse;
import com.help.desk.user.dto.request.CreateUserRequest;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.enums.UserRole;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import com.help.desk.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(
            @PathVariable UserRole role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/status/{active}")
    public ResponseEntity<List<UserResponse>> getUsersByStatus(
            @PathVariable Boolean active) {
        return ResponseEntity.ok(userService.getUsersByActive(active));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<String> activateUser(
            @PathVariable Long id) {
        userService.activeUser(id);
        return ResponseEntity.ok("User activated successfully");
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long id) {
        userService.deactiveUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }


}