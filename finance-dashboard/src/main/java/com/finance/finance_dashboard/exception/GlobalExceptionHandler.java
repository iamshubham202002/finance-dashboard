// exception/GlobalExceptionHandler.java
package com.finance.finance_dashboard.exception;

import com.finance.finance_dashboard.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleGenericException(Exception e) {
        return ApiResponse.error("An error occurred: " + e.getMessage());
    }
}