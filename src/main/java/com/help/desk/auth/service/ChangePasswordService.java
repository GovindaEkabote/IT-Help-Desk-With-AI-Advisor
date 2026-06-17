package com.help.desk.auth.service;

import com.help.desk.auth.dto.request.ChangePasswordRequest;

public interface ChangePasswordService {

    void changePassword(ChangePasswordRequest request);
}
