package com.tablefour.sidequest.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError<E> {

    private Integer status;

    private ErrorMessageType errorMessageType;

    private Exception<E> exception;
}