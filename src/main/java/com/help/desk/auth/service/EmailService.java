package com.help.desk.auth.service;

import com.help.desk.auth.dto.request.ForgotPasswordRequest;
import com.help.desk.auth.dto.request.ResetPasswordRequest;
import jakarta.transaction.Transactional;

public interface EmailService {

    void sendOtp(String email, String otp);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);
}
