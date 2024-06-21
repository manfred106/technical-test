package org.test.exception;

public class InvalidCountryCodeException extends RuntimeException {

    public InvalidCountryCodeException(String message) {
        super(message);
    }

}
