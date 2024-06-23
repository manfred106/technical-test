package org.test.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartException;

import static org.assertj.core.api.Assertions.assertThat;

class MultipartExceptionHandlerTest {

    private final MultipartExceptionHandler exceptionHandler = new MultipartExceptionHandler();

    @Test
    void whenExceptionThrown_thenReturnNotAcceptable() {
        // given
        String message = "Current request is not a multipart request";
        MultipartException exception = new MultipartException(message);

        // when
        ErrorResponse response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}