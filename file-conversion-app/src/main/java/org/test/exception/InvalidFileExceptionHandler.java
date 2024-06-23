package org.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InvalidFileExceptionHandler implements ExceptionHandlerSupport<InvalidFileException> {

    @ExceptionHandler(InvalidFileException.class)
    @Override
    public ErrorResponse handleException(InvalidFileException exception) {
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .type(EMPTY_URI)
                .build();
    }

}