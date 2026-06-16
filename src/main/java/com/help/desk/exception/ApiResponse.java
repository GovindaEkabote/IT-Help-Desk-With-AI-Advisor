package com.help.desk.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ApiResponse {

    private LocalDateTime timestamp;
    private int status;
    private boolean success;
    private String message;
}
