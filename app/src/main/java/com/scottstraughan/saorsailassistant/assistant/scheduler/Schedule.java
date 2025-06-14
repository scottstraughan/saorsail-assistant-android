package com.scottstraughan.saorsailassistant.assistant.scheduler;

import java.text.ParseException;

/**
 * Enum to map schedule times.
 */
public enum Schedule {
    TEST,
    FIVE_MINUTES,
    FIFTEEN_MINUTES,
    THIRTY_MINUTES,
    HOURLY,
    DAILY;

    /**
     * Convert an enum value into a millisecond value, something that is useful in other bits of
     * code.
     */
    public static long toMilliseconds(Schedule interval) throws ParseException {
        switch (interval) {
            case TEST:
                return 60000;
            case FIVE_MINUTES:
                return 300000;
            case FIFTEEN_MINUTES:
                return 900000;
            case THIRTY_MINUTES:
                return 1800000;
            case HOURLY:
                return 3600000;
            case DAILY:
                return 86400000;
        }

        throw new ParseException("Cannot parse CheckInterval.", 0);
    }
}
