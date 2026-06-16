package com.help.desk.user.dto.response;

import com.help.desk.user.enums.UserRole;
import com.help.desk.user.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String employeeId;

    private String phone;

    private String address;

    private String profileImageUrl;

    private String department;

    private UserRole role;

    private UserStatus status;

    private Boolean active;

    private String managerName;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
