package org.example.weather.support;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Логирование расхождений «ожидаемое vs фактическое» в stdout-лог.
 * Шаги вызывают этот хелпер рядом с AssertJ-проверками, чтобы выполнить
 * требование ТЗ вывести расхождения по каждому значению в лог.
 * <p>
 * key - произвольный идентификатор сравниваемого случая (имя города,
 * код ошибки и т.п.), попадающий в префикс строки для контекста.
 */
public final class Mismatches {

    private static final Logger log = LoggerFactory.getLogger(Mismatches.class);

    private Mismatches() {
    }

    public static void report(
            final String key,
            final String field,
            final Object expected,
            final Object actual
    ) {
        if (!Objects.equals(expected, actual)) {
            log.warn("[{}] {} mismatch: expected={}, actual={}", key, field, expected, actual);
        }
    }

}
