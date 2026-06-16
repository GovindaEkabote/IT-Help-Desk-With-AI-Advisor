package com.help.desk.user.mapper;

import com.help.desk.user.dto.response.UserResponse;
import com.help.desk.user.model.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .employeeId(user.getEmployeeId())
                .phone(user.getPhone())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .department(user.getDepartment())
                .role(user.getRole())
                .status(user.getStatus())
                .active(user.getActive())
                .managerName(
                        user.getReportingManager() != null
                                ? user.getReportingManager().getFirstName() + " "
                                + user.getReportingManager().getLastName()
                                : null
                )
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}