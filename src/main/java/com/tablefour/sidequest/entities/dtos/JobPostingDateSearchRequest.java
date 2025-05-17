package com.tablefour.sidequest.entities.dtos;

import com.tablefour.sidequest.entities.enums.DateFieldType;
import com.tablefour.sidequest.entities.enums.DateSearchType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobPostingDateSearchRequest {
    @NotNull(message = "Date field type is required")
    private DateFieldType fieldType;

    @NotNull(message = "Search type is required")
    private DateSearchType searchType;

    @NotNull(message = "Date is required")
    private LocalDateTime date;
}