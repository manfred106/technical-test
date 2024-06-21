package org.test.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.test.exception.FileFormatException;
import org.test.exception.InvalidCountryCodeException;
import org.test.exception.InvalidIspException;

import java.net.URI;

@ControllerAdvice
public class ExceptionHandlingController {

    private static final URI EMPTY_URI = URI.create("");

    @ExceptionHandler(MultipartException.class)
    public ErrorResponse handleMultipartException(MultipartException exception) {
        return ErrorResponse.builder(exception, HttpStatus.NOT_ACCEPTABLE, "Current request is not a multipart request")
                .type(EMPTY_URI)
                .build();
    }

    @ExceptionHandler(FileFormatException.class)
    public ErrorResponse handleFileFormatException(FileFormatException exception) {
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage())
                .type(EMPTY_URI)
                .build();

    }

    @ExceptionHandler(InvalidCountryCodeException.class)
    public ErrorResponse handleInvalidCountryCodeException(InvalidCountryCodeException exception) {
        return ErrorResponse.builder(exception, HttpStatus.FORBIDDEN, exception.getMessage())
                .type(EMPTY_URI)
                .build();
    }

    @ExceptionHandler(InvalidIspException.class)
    public ErrorResponse handleInvalidIspException(InvalidIspException exception) {
        return ErrorResponse.builder(exception, HttpStatus.FORBIDDEN, exception.getMessage())
                .type(EMPTY_URI)
                .build();
    }

}