package com.help.desk.user.service;

import com.help.desk.user.dto.request.CreateUserRequest;
import com.help.desk.user.dto.request.UpdateRoleRequest;
import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.enums.UserRole;
import com.help.desk.user.model.User;
import org.springframework.data.domain.Page;


import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByEmployeeId(String employeeId);

    Page<UserResponse> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    UserResponse updateUser(Long id, CreateUserRequest request);

    void deleteUser(Long id);

    List<UserResponse> getUsersByRole(UserRole role);

    List<UserResponse> getUsersByActive(Boolean active);

    void activeUser(Long id);

    void deactiveUser(Long id);

    UserResponse updateRole(Long id, UpdateRoleRequest role);

}