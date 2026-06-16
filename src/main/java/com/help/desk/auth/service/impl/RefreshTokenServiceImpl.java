package com.help.desk.auth.service.impl;

import com.help.desk.auth.model.RefreshToken;
import com.help.desk.auth.repository.RefreshTokenRepository;
import com.help.desk.auth.service.RefreshTokenService;
import com.help.desk.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiryDate(
                                Instant.now()
                                        .plus(30, ChronoUnit.DAYS)
                        )
                        .revoked(false)
                        .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyRefreshToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(
                                () -> new RuntimeException("Invalid Token")
                        );
        if (refreshToken.getRevoked()){
            throw new RuntimeException("Token revoked");
        }
        if (refreshToken.getExpiryDate().isBefore(Instant.now())){
            throw new RuntimeException("Expired token");
        }

        return refreshToken;
    }

    @Override
    public void revokeUserTokens(User user) {
            refreshTokenRepository.deleteByUser(user);
    }
}
