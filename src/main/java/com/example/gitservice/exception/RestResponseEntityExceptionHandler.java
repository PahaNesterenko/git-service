package com.example.gitservice.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleNotFoundException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ExceptionPayload(HttpStatus.NOT_FOUND.value(), "Resource not found"), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {GitServiceCommonException.class})
    protected ResponseEntity<Object> handleCommonException(RuntimeException ex, WebRequest request) {
        return handleExceptionInternal(ex, new ExceptionPayload(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Exception occurred"), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @SneakyThrows
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return handleExceptionInternal(ex, new ObjectMapper().writer().writeValueAsString(new ExceptionPayload(status.value(), "Not acceptable response type")), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
