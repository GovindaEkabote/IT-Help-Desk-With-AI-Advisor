package com.help.desk.ai.dto;

import lombok.Data;

@Data
public class ChatbotRequest {

    private String message;
    private Long userId;
    private Long ticketId;
}
