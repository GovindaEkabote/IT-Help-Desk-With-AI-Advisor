package com.help.desk.auth.service;

import com.help.desk.auth.model.RefreshToken;
import com.help.desk.user.model.User;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken(User user);

    RefreshToken verifyRefreshToken(String token);

    void revokeUserTokens(User user);
}
