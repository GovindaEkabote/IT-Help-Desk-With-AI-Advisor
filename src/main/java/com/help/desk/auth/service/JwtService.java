package com.help.desk.auth.service;

import com.help.desk.user.model.User;

public interface JwtService {

    String generateAccessToken(User user);

    String extractUsername(String  token);

    boolean validateToken(String token);

}
