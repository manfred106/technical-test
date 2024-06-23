package org.test.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.client.RestClientException;

import static org.assertj.core.api.Assertions.assertThat;

class RestClientExceptionHandlerTest {

    private final RestClientExceptionHandler exceptionHandler = new RestClientExceptionHandler();

    @Test
    void whenExceptionThrown_thenReturnInternalServerError() {
        // given
        String message = "Error connecting to 3rd party service";
        RestClientException exception = new RestClientException(message);

        // when
        ErrorResponse response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}