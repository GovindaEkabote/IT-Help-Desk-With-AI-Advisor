package com.help.desk.ai.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatbotResponse {

    private String response;
    private List<String> sources;
    private boolean hasKnowledgeBaseMatch;
}
