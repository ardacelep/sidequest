package com.tablefour.sidequest.core.results;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum MessageType {

    CREATED("CREATED", "Data was successfully created and saved to the database."),
    FOUND("FOUND", "Data was successfully retrieved from the database."),
    COMPLETED("COMPLETED", "Process was successfully completed."),
    UPDATED("UPDATED", "Data was successfully updated in the database."),
    DELETED("DELETED", "Data was successfully deleted from the database."),;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("message")
    private final String message;

    MessageType(String name, String message) {
        this.name = name;
        this.message = message;
    }

}
