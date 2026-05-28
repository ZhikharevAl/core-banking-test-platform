package org.example.weather.support;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Логирование расхождений «ожидаемое vs фактическое» в stdout-лог.
 */
public final class Mismatches {

    private static final Logger log = LoggerFactory.getLogger(Mismatches.class);

    private Mismatches() {
    }

    /**
     * Сравнивает ожидаемое и фактическое значение, при расхождении пишет WARN в лог.
     */
    public static boolean report(
            final String key,
            final String field,
            final Object expected,
            final Object actual
    ) {
        if (Objects.equals(expected, actual)) {
            return false;
        }
        log.warn("[{}] {} mismatch: expected={}, actual={}", key, field, expected, actual);
        return true;
    }

}
