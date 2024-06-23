package org.test.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidFileExceptionHandlerTest {

    private final InvalidFileExceptionHandler exceptionHandler = new InvalidFileExceptionHandler();

    @Test
    void whenExceptionThrown_thenReturnBadRequest() {
        // given
        String message = "File is not valid";
        InvalidFileException exception = new InvalidFileException(message);

        // when
        ErrorResponse response = exceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getDetail()).isEqualTo(message);
    }

}