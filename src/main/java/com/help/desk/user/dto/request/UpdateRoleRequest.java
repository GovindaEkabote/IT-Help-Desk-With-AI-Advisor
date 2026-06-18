package com.help.desk.user.dto.request;

import com.help.desk.user.enums.UserRole;

public record UpdateRoleRequest(
        UserRole role
) {
}
