package org.test.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidIspExceptionHandlerTest {
    private final InvalidIspExceptionHandler exceptionHandler = new InvalidIspExceptionHandler();

    @Test
    void whenExceptionThrown_thenReturnForbidden() {
        // given
        String message = "This ISP is blocked from access";
        InvalidIspException exception = new InvalidIspException(message);

        // when
        ErrorResponse response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}