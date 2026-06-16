package com.help.desk.auth.service.impl;

import com.help.desk.auth.dto.request.ForgotPasswordRequest;
import com.help.desk.auth.dto.request.ResetPasswordRequest;
import com.help.desk.auth.model.PasswordResetOtp;
import com.help.desk.auth.repository.PasswordResetOtpRepository;
import com.help.desk.auth.service.EmailService;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void sendOtp(String email, String otp) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Password Reset OTP");
        simpleMailMessage.setText(
                "Your OTP is: " + otp + "\nValid for 10 minutes"
        );

        javaMailSender.send(simpleMailMessage);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        String otp = String.valueOf(
                ThreadLocalRandom.current().nextInt(100000, 999999)
        );

        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setEmail(user.getEmail());
        passwordResetOtp.setOtp(otp);
        passwordResetOtp.setExpireTime(LocalDateTime.now().plusMinutes(10));
        passwordResetOtp.setUsed(false);

        passwordResetOtpRepository.save(passwordResetOtp);

        sendOtp(user.getEmail(), otp);
    }


    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
            PasswordResetOtp resetOtp =
                    passwordResetOtpRepository.findTopByEmailAndOtpAndUsedFalseOrderByCreatedAtDesc(
                            request.getEmail(),
                            request.getOtp()
                    )
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

            if(resetOtp.getExpireTime().isBefore(LocalDateTime.now())){
                throw new RuntimeException("OTP expired");
            }

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

            user.setPassword(
                    passwordEncoder.encode(request.getNewPassword())
            );
            resetOtp.setUsed(true);
            passwordResetOtpRepository.save(resetOtp);
    }
}
