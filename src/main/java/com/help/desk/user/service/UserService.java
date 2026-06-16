package com.help.desk.user.service;

import com.help.desk.user.dto.request.CreateUserRequest;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.model.User;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByEmployeeId(String employeeId);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, CreateUserRequest request);

    void deleteUser(Long id);;
}