package com.help.desk.auth.service.impl;


import com.help.desk.auth.dto.request.LoginRequest;
import com.help.desk.auth.dto.request.RefreshTokenRequest;
import com.help.desk.auth.dto.response.AuthResponse;
import com.help.desk.auth.model.RefreshToken;
import com.help.desk.auth.repository.RefreshTokenRepository;
import com.help.desk.auth.service.AuthService;
import com.help.desk.auth.service.JwtService;
import com.help.desk.auth.service.RefreshTokenService;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(
                request.getEmail()
        ).orElseThrow();

        return generateAuthResponse(user);
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken =
                refreshTokenService.verifyRefreshToken(
                        request.getRefreshToken()
                );
        User user = refreshToken.getUser();

        RefreshToken refreshToken1 =
                refreshTokenService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(
                        jwtService.generateAccessToken(user)
                )
                .refreshToken(refreshToken1.getToken())
                .tokenType("Bearer")
                .expiresIn(900L)
                .role(user.getRole().name())
                .build();
    }

    @Override
    public void logout(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Token");
        }

        String jwt = authHeader.substring(7);

        String email = jwtService.extractUsername(jwt);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.deleteByUser(user);
    }

    private AuthResponse generateAuthResponse(User user) {
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(900L)
                .role(user.getRole().name())
                .build();
    }
}
