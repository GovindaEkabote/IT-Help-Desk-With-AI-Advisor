package com.help.desk.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        String phone,

        String employeeId
) {
}
