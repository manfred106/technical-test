package org.test.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartException;
import org.test.exception.FileFormatException;
import org.test.exception.InvalidCountryCodeException;
import org.test.exception.InvalidIspException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionHandlingControllerTest {

    private final ExceptionHandlingController exceptionHandlingController = new ExceptionHandlingController();

    @Test
    void testHandleMultipartException() {
        // given
        MultipartException exception = new MultipartException("");

        // when
        ErrorResponse response = exceptionHandlingController.handleMultipartException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody().getDetail()).isEqualTo("Current request is not a multipart request");
    }

    @Test
    void testFileFormatException() {
        // given
        String message = "Input file is not in correct format";
        FileFormatException exception = new FileFormatException(message, null);

        // when
        ErrorResponse response = exceptionHandlingController.handleFileFormatException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    void testInvalidCountryCodeException() {
        // given
        String message = "This country is blocked from access";
        InvalidCountryCodeException exception = new InvalidCountryCodeException(message);

        // when
        ErrorResponse response = exceptionHandlingController.handleInvalidCountryCodeException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

    @Test
    void testInvalidIspException() {
        // given
        String message = "This ISP is blocked from access";
        InvalidIspException exception = new InvalidIspException(message);

        // when
        ErrorResponse response = exceptionHandlingController.handleInvalidIspException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}