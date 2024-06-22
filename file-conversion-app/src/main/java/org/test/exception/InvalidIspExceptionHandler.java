package org.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidIspExceptionHandler implements ExceptionHandlerSupport<InvalidIspException> {

    @ExceptionHandler(InvalidIspException.class)
    @Override
    public ErrorResponse handleException(InvalidIspException exception) {
        return ErrorResponse.builder(exception, HttpStatus.FORBIDDEN, exception.getMessage())
                .type(EMPTY_URI)
                .build();

    }

}