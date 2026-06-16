package com.help.desk.auth.service;

import com.help.desk.auth.dto.request.LoginRequest;
import com.help.desk.auth.dto.request.RefreshTokenRequest;
import com.help.desk.auth.dto.response.AuthResponse;
import com.help.desk.user.model.User;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(
            RefreshTokenRequest request);

    void logout(String refreshToken);

    User getCurrentUser();
}
