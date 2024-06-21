package org.test.util;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * For testing purpose, as Mockito cannot mock static method LocalDateTime.now()
 */
@Component
public class LocalDateTimeProvider {

    private static final Clock DEFAULT_CLOCK = Clock.systemDefaultZone();

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(DEFAULT_CLOCK);
    }

}