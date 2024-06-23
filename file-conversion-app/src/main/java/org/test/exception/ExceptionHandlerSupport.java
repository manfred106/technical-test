package org.test.exception;

import org.springframework.web.ErrorResponse;

import java.net.URI;

public interface ExceptionHandlerSupport<T extends Exception> {

    URI EMPTY_URI = URI.create("");

    ErrorResponse handleException(T exception);

}
