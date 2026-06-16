package com.help.desk.user.service.impl;

import com.help.desk.exception.DuplicateResourceException;
import com.help.desk.exception.ResourceNotFoundException;
import com.help.desk.user.dto.request.CreateUserRequest;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.enums.UserRole;
import com.help.desk.user.mapper.UserMapper;
import com.help.desk.user.model.User;
import com.help.desk.user.repository.UserRepository;
import com.help.desk.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder =  passwordEncoder;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        // Map request to User entity
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phone(request.phone())
                .employeeId(request.employeeId())
                .role(UserRole.EMPLOYEE)
                .active(true)
                .deleted(false)
                .build();

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        if (user.getPhone() != null && userRepository.existsByPhone(user.getPhone())) {
            throw new DuplicateResourceException("Phone already exists");
        }
        if (user.getEmployeeId() != null && userRepository.existsByEmployeeId(user.getEmployeeId())) {
            throw new DuplicateResourceException("Employee ID already exists");
        }

        User savedUser = userRepository.save(user);
        return UserMapper.toResponse(savedUser);
    }



    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + id));
        return UserMapper.toResponse(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    public User getUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with employeeId: " + employeeId));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAllByDeletedFalse();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return users.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public UserResponse updateUser(Long id, CreateUserRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("Email already exists");
        }

        if (request.phone() != null && userRepository.existsByPhoneAndIdNot(request.phone(), id)) {
            throw new DuplicateResourceException("Phone already exists");
        }

        if (request.employeeId() != null && userRepository.existsByEmployeeIdAndIdNot(request.employeeId(), id)) {
            throw new DuplicateResourceException("Employee ID already exists");
        }

        existingUser.setFirstName(request.firstName());
        existingUser.setLastName(request.lastName());
        existingUser.setEmail(request.email());
        existingUser.setPhone(request.phone());
        existingUser.setEmployeeId(request.employeeId());

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Soft delete instead of hard delete
        user.setDeleted(true);
        user.setActive(false);
        userRepository.save(user);
    }
}
