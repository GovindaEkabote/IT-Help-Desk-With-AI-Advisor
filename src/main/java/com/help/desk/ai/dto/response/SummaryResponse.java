package com.help.desk.ai.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryResponse {
    private String summary;
    private Double confidence;

}
