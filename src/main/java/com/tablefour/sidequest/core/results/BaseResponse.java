package com.tablefour.sidequest.core.results;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<D> {

    private HttpStatus httpStatus;

    private Integer httpStatusCode;

    private MessageType messageType;

    private String hostName;

    private String path;

    private String requestType;

    private LocalDateTime createdAt;

    private String message;

    private final Map<String, D> data = new HashMap<>();

    @JsonAnyGetter
    public Map<String, D> getData() {
        return data;
    }

    // Opsiyonel: Kolay veri eklemek için
    public void addData(String key, D value) {
        this.data.put(key, value);
    }
}

