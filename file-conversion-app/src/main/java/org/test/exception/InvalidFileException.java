package org.test.exception;

import java.io.IOException;

public class InvalidFileException extends IOException {

    public InvalidFileException(String message) {
        super(message);
    }

    public InvalidFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
