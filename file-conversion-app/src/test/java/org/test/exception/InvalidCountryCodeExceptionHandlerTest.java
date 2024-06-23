package org.test.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidCountryCodeExceptionHandlerTest {

    private final InvalidCountryCodeExceptionHandler exceptionHandler = new InvalidCountryCodeExceptionHandler();

    @Test
    void whenExceptionThrown_thenReturnForbidden() {
        // given
        String message = "This country is blocked from access";
        InvalidCountryCodeException exception = new InvalidCountryCodeException(message);

        // when
        ErrorResponse response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}