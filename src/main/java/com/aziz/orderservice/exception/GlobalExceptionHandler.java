package com.aziz.orderservice.exception;

import com.aziz.orderservice.error.ErrorObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Global exception handler for out of stock exception.
     * @param ex
     * @param request
     * @return Exception response with error message
     */
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorObject> outOfStockException(OutOfStockException ex, WebRequest request) {
        ErrorObject message = ErrorObject.builder()
                .date(new Date())
                .message(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
        System.out.println("THe message is " + ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }
}
