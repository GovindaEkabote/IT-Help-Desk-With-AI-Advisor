package com.help.desk.tickets.dto.response;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;

    private int page;
    private int size;

    private long totalElements;
    private int totalPages;

    private boolean first;
    private boolean last;
}
