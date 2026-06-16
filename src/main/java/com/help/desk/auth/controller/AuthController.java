package com.help.desk.auth.controller;


import com.help.desk.auth.dto.request.LoginRequest;
import com.help.desk.auth.dto.request.RefreshTokenRequest;
import com.help.desk.auth.dto.response.AuthResponse;
import com.help.desk.auth.service.AuthService;
import com.help.desk.exception.ApiError;
import com.help.desk.exception.ApiResponse;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import com.help.desk.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/auth")
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(
            @RequestHeader("Authorization") String authHeader) {

        authService.logout(authHeader);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(200)
                        .success(true)
                        .message("Logout successful")
                        .build()
        );
    }


    @GetMapping("/current-user")
    public ResponseEntity<UserResponse> getCurrentUser() {

        User user = authService.getCurrentUser();

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();

        return ResponseEntity.ok(response);
    }

}
