package org.example.backend.exception;

import org.example.backend.exception.requestError.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorEntity> handleBusinessException(BusinessException ex) {
        ErrorEntity error = ErrorEntity.builder()
                .errorCode(Integer.parseInt(ex.getErrorCode().getCode()))
                .errorMessage(ex.getMessage())
                .build();

        return ResponseEntity.status(ex.getErrorCode().getHttpStatus())
                .body(error);
    }
}