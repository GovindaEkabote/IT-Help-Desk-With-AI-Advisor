package com.help.desk.auth.service;

import com.help.desk.auth.dto.request.LoginRequest;
import com.help.desk.auth.dto.request.RefreshTokenRequest;
import com.help.desk.auth.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(
            RefreshTokenRequest request);

    void logout(String refreshToken);
}
