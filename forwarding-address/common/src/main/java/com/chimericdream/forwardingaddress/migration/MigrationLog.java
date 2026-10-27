package com.chimericdream.forwardingaddress.migration;

import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Logs a problem once per key, so a thousand unconvertible blocks don't write a thousand identical
 * lines. Conversions that need the exact position (block entities) log every time through
 * {@link #logger()} instead.
 */
public final class MigrationLog {
    private final Logger logger;
    private final Set<String> seen = ConcurrentHashMap.newKeySet();

    public MigrationLog(Logger logger) {
        this.logger = logger;
    }

    public Logger logger() {
        return logger;
    }

    public void info(String key, String message, Object... args) {
        if (seen.add(key)) {
            logger.info(message, args);
        }
    }

    public void warn(String key, String message, Object... args) {
        if (seen.add(key)) {
            logger.warn(message, args);
        }
    }

    public void error(String key, String message, Object... args) {
        if (seen.add(key)) {
            logger.error(message, args);
        }
    }
}
