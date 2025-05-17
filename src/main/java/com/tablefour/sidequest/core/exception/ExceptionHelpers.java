package com.tablefour.sidequest.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExceptionHelpers {

    public <E> ApiError<E> createApiError(E message, WebRequest webRequest, HttpStatus httpStatus, ErrorMessageType errorMessageType) {

        ApiError<E> apiError = new ApiError<>();

        apiError.setStatus(httpStatus.value());

        apiError.setErrorMessageType(errorMessageType);

        Exception exception = new Exception<>();

        exception.setCreatedAt(LocalDateTime.now());

        exception.setHostName(getHostName());

        exception.setPath(webRequest.getDescription(false).substring(4));

        // WebRequest'i ServletWebRequest'e cast et
        if (webRequest instanceof ServletWebRequest servletWebRequest) {
            HttpServletRequest request = servletWebRequest.getRequest();
            String methodType = request.getMethod(); // GET, POST, PATCH vb.
            exception.setRequestType(methodType);
        }

        exception.setMessage(message);

        apiError.setException(exception);

        return apiError;


    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();


        } catch (UnknownHostException e) {
            System.out.println("hata oluştu " + e.getMessage());
        }

        return null;

    }

    public List<String> addMapValue(List<String> list, String newValue) {
        list.add(newValue);
        return list;
    }

    public ResponseEntity<ApiError<Map<String, List<String>>>> prepareFieldValidationExceptionResponse(MethodArgumentNotValidException ex, WebRequest webRequest, ErrorMessageType errorMessageType) {

        Map<String, List<String>> errorsMap = new HashMap<>();

        for (ObjectError objError : ex.getBindingResult().getAllErrors()) {
            String fieldName = ((FieldError) objError).getField();

            if (errorsMap.containsKey(fieldName)) {
                errorsMap.put(fieldName, addMapValue(errorsMap.get(fieldName), objError.getDefaultMessage()));

            }
            else {
                errorsMap.put(fieldName, addMapValue(new ArrayList<>(), objError.getDefaultMessage()));

            }
        }

        return ResponseEntity.status(ex.getStatusCode().value()).body(createApiError(errorsMap, webRequest, HttpStatus.BAD_REQUEST,errorMessageType));

    }

    public ResponseEntity<ApiError> prepareRunTimeBaseExceptionResponse(RuntimeBaseException exception, WebRequest webRequest){

        return ResponseEntity.status(exception.getHttpStatus()).body(createApiError(exception.getMessage(), webRequest, exception.getHttpStatus(), exception.getErrorMessageType()));

    }

    public ResponseEntity<ApiError> prepareHttpMessageNotReadableExceptionResponse(HttpMessageNotReadableException exception, WebRequest webRequest){
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(),webRequest, HttpStatus.BAD_REQUEST, ErrorMessageType.MESSAGE_NOT_READABLE ));
    }

    public ResponseEntity<ApiError> prepareMethodArgumentTypeMismatchExceptionResponse(MethodArgumentTypeMismatchException exception, WebRequest webRequest){
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(),webRequest,HttpStatus.BAD_REQUEST,ErrorMessageType.MESSAGE_NOT_READABLE));
    }

    public ResponseEntity<ApiError<Map<String, List<String>>>> prepareMissingParameterExceptionResponse(
            MissingServletRequestParameterException ex, WebRequest webRequest, ErrorMessageType errorMessageType) {

        Map<String, List<String>> errorsMap = new HashMap<>();

        String paramName = ex.getParameterName();
        String errorMessage = "Required parameter '" + paramName + "' is missing";

        errorsMap.put(paramName, List.of(errorMessage));

        return ResponseEntity.badRequest().body(createApiError(errorsMap, webRequest, HttpStatus.BAD_REQUEST, errorMessageType));
    }

    public ResponseEntity<ApiError<Map<String, List<String>>>> prepareMissingPathVariableExceptionResponse(
            MissingPathVariableException ex, WebRequest webRequest, ErrorMessageType errorMessageType) {

        Map<String, List<String>> errorsMap = new HashMap<>();

        String variableName = ex.getVariableName();
        String errorMessage = "Required path variable '" + variableName + "' is missing";

        errorsMap.put(variableName, List.of(errorMessage));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(createApiError(errorsMap, webRequest, HttpStatus.BAD_REQUEST, errorMessageType));
    }

    public ResponseEntity<ApiError<String>> prepareNoResourceFoundExceptionResponse(
            NoResourceFoundException ex, WebRequest webRequest, ErrorMessageType errorMessageType) {

        String errorMessage = "The requested resource was not found: " + webRequest.getDescription(false).substring(4);

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(createApiError(errorMessage, webRequest, HttpStatus.NOT_FOUND, errorMessageType));
    }


}

