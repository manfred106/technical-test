package org.test.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class RestClientExceptionHandler implements ExceptionHandlerSupport<RestClientException> {

    @ExceptionHandler(MultipartException.class)
    @Override
    public ErrorResponse handleException(RestClientException exception) {
        return ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage())
                .type(EMPTY_URI)
                .build();
    }

}