package com.help.desk.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChangePasswordRequest {

    @NotBlank
    private String currentPassword;
    @NotBlank
    private String newPassword;
}
