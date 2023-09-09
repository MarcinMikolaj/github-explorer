package com.explorer.infrastructure.handler;

import com.explorer.infrastructure.exceptions.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers,
                                                                      HttpStatusCode status, WebRequest request) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .headers(header -> header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .body(prepareExceptionDto(HttpStatus.NOT_ACCEPTABLE, ex.getMessage()));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResponseException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseException ex, WebRequest request){
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .headers(header -> header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .body(prepareExceptionDto(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    private ExceptionDto prepareExceptionDto(HttpStatus status, String message){
        return ExceptionDto.builder()
                .statusCode(status.value())
                .message(message)
                .build();
    }

}
