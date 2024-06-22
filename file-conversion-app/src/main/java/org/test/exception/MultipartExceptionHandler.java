package org.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class MultipartExceptionHandler implements ExceptionHandlerSupport<MultipartException> {

    @ExceptionHandler(MultipartException.class)
    @Override
    public ErrorResponse handleException(MultipartException exception) {
        return ErrorResponse.builder(exception, HttpStatus.NOT_ACCEPTABLE, "Current request is not a multipart request")
                .type(EMPTY_URI)
                .build();
    }

}