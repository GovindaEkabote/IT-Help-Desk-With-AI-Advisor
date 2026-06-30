package com.help.desk.ai.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SuggestionResponse {
    private String advice;
    private String category;
}
