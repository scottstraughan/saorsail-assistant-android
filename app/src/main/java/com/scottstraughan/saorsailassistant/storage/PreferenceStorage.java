package com.scottstraughan.saorsailassistant.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.scottstraughan.saorsailassistant.assistant.scheduler.Schedule;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.logging.Logger;

/**
 * Preference storage wrapper.
 */
public class PreferenceStorage {
    /**
     * Default check interval.
     */
    static Schedule DEFAULT_CHECK_INTERVAL = Schedule.FIFTEEN_MINUTES;

    /**
     * Check if the check service is enabled or not.
     * @param context context to use
     * @return true if enabled
     */
    public static boolean isAssistantEnabled(
        Context context
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getBoolean(
                context.getString(R.string.preference_key_assistant_enabled_key), true);
    }

    /**
     * Get the check interval time in milliseconds.
     * @param context the context to use
     * @return check interval time
     */
    public static Schedule getCheckInterval(
        Context context
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        try {
            String interval = sharedPref.getString(
                    context.getString(R.string.preference_key_assistant_check_interval), null);

            if (interval == null) {
                return PreferenceStorage.DEFAULT_CHECK_INTERVAL;
            }

            switch (interval) {
                case "1_MIN":
                    return Schedule.TEST;
                case "5_MIN":
                    return Schedule.FIVE_MINUTES;
                case "15_MIN":
                    return Schedule.FIFTEEN_MINUTES;
                case "30_MIN":
                    return Schedule.THIRTY_MINUTES;
                case "HOURLY":
                    return Schedule.HOURLY;
                case "DAILY":
                    return Schedule.DAILY;
            }
        } catch (Exception ignored) {
            Logger.e("Could not parse check interval from data store.");
        }

        return PreferenceStorage.DEFAULT_CHECK_INTERVAL;
    }

    /**
     * Get the check interval time in milliseconds.
     * @param context the context to use
     * @return check interval time
     */
    @Nullable
    public static String getPairCode(
        Context context
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(
                context.getString(R.string.preference_key_assistant_pair_code), null);
    }

    /**
     * Get the location to store APKs.
     * @param context the context to use
     * @return location to store downloads
     */
    @Nullable
    public static String getDownloadApkDirectory(
        Context context
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPref.getString(context.getString(
                R.string.preference_key_download_location), null);
    }

    /**
     * @param context context to use
     * @param value
     */
    public static void setDownloadApkDirectory(
        Context context,
        String value
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(context.getString(
                R.string.preference_key_download_location), value);

        editor.apply();
    }

    /**
     * Check the URL endpoint to check for app installs.s
     * @return the check Url
     */
    public static String getAppInstallCheckUrl(
        Context context
    ) {
        String defaultValue =  "https://saorsail-main-6e1e5bc.d2.zuplo.dev";

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        String preferenceValue =  sharedPref.getString(
            context.getString(R.string.preference_key_check_endpoint), defaultValue);

        if (preferenceValue.isEmpty()) {
            preferenceValue = defaultValue;
        }

        return preferenceValue;
    }

    /**
     * Determine if a device pair code is valid.
     * @return true if value, false otherwise.
     */
    public static boolean isPairCodeValid(
        Context context
    ) {
        String pairCode = getPairCode(context);

        if (pairCode == null) {
            return false;
        }

        return pairCode.length() == 64;
    }

    /**
     * Set if notifications are enabled.
     * @param context context to use
     * @param enabled enabled or not
     */
    public static void setNotificationsEnabled(
        Context context,
        boolean enabled
    ) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putBoolean(context.getString(
                R.string.preference_key_notifications_enabled), enabled);

        editor.apply();
    }
}