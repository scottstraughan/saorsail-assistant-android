package com.scottstraughan.saorsailassistant.logging;

import android.util.Log;

/**
 * Default logging class.
 */
public class Logger {
    /**
     * Default tag.
     */
    public static final String TAG = "SAORSAIL";

    /**
     * Enabled or disabled
     */
    private static final boolean ENABLED = true;

    public static void i(String message) {
        Logger.i(TAG, message);
    }

    public static void i(String tag, String message) {
        if (!ENABLED) {
            return;
        }

        Log.i(tag, message);
    }

    public static void w(String message) {
        Logger.w(TAG, message);
    }

    public static void w(String tag, String message) {
        if (!ENABLED) {
            return;
        }

        Log.w(tag, message);
    }

    public static void e(String message) {
        Logger.e(TAG, message);
    }

    public static void e(String tag, String message) {
        if (!ENABLED) {
            return;
        }

        Log.e(tag, message);
    }
}
