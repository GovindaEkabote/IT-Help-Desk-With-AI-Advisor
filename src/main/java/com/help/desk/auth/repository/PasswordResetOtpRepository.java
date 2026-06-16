package com.help.desk.auth.repository;

import com.help.desk.auth.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    Optional<PasswordResetOtp> findTopByEmailAndOtpAndUsedFalseOrderByCreatedAtDesc(String email, String otp);
}
