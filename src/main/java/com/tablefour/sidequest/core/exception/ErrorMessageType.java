package com.tablefour.sidequest.core.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum ErrorMessageType {

    NO_RECORD_EXISTS("NO_RECORD_EXISTS","Such record does not exist in the database."),
    VALIDATION_FAILED("VALIDATION_FAILED", "Properties sent in json could not be validated."),
    RECORD_ALREADY_EXISTS("RECORD_ALREADY_EXISTS","A similar record has been made before."),
    MESSAGE_NOT_READABLE("MESSAGE_NOT_READABLE","Message could not be read."),
    ALREADY_RESERVED("ALREADY_RESERVED","This instance is already reserved."),
    ALREADY_CANCELLED("ALREADY_CANCELLED","This instance is already cancelled."),
    ALREADY_ACTIVE("ALREADY_ACTIVE","This instance is already active."),
    MISSING_PATH_VARIABLE("MISSING_PATH_VARIABLE","There is a missing path variable."),
    MISSING_REQUEST_PARAM("MISSING_REQUEST_PARAM","There is a missing request parameter."),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND","There is no such endpoint."),
    MAIL_ERROR("MAIL_ERROR","An error occurred while sending the email."),
    INVALID_TOKEN("INVALID_TOKEN","The token is not valid."),
    EXPIRED_TOKEN("EXPIRED_TOKEN","The token is expired."),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS","The credentials are not valid."),;



    @JsonProperty("name")
    private final String name;

    @JsonProperty("message")
    private final String message;

    ErrorMessageType(String name ,String message){
        this.message = message;
        this.name = name;
    }

}

