package com.tablefour.sidequest.core.results;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageResponse<T> {
    private HttpStatus httpStatus;
    private Integer httpStatusCode;
    private MessageType messageType;
    private String hostName;
    private String path;
    private String requestType;
    private LocalDateTime createdAt;
    private String message;

    // Pagination specific fields
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> of(Page<T> page, HttpStatus status, MessageType messageType,
            String message, String hostName, String path, String requestType) {
        PageResponse<T> response = new PageResponse<>();
        response.setHttpStatus(status);
        response.setHttpStatusCode(status.value());
        response.setMessageType(messageType);
        response.setMessage(message);
        response.setHostName(hostName);
        response.setPath(path);
        response.setRequestType(requestType);
        response.setCreatedAt(LocalDateTime.now());

        // Set pagination data
        response.setContent(page.getContent());
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());

        return response;
    }
}