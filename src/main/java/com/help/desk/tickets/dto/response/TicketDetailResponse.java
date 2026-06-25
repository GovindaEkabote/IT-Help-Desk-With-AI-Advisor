package com.help.desk.tickets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDetailResponse {
    private TicketResponse ticketResponse;
    private List<CommentResponse> ticket;
    private List<TicketHistoryResponse> history;
    private Integer totalComments;
    private Integer totalHistoryEntries;

}
