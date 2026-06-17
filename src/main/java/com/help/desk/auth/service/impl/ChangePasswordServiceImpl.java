package com.help.desk.auth.service.impl;

import com.help.desk.auth.dto.request.ChangePasswordRequest;
import com.help.desk.auth.service.AuthService;
import com.help.desk.auth.service.ChangePasswordService;
import com.help.desk.exception.BadRequestException;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ChangePasswordServiceImpl implements ChangePasswordService {

    private final AuthService authService;
    private final PasswordEncoder  passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = authService.getCurrentUser();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )){
            throw new BadRequestException("Current password is Incorrect");
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())

        );
        userRepository.save(user);
    }
}
