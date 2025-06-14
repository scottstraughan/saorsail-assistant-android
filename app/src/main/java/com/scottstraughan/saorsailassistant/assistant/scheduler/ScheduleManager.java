package com.scottstraughan.saorsailassistant.assistant.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import androidx.annotation.NonNull;

import com.scottstraughan.saorsailassistant.assistant.AssistantService;
import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.storage.PreferenceStorage;

import java.text.ParseException;

/**
 * Check for broadcasts to check for installs.
 */
public class ScheduleManager extends BroadcastReceiver {
    static String WAKELOCK_NAME = "SAA_WAKELOCK";

    private static AssistantService assistantService;

    /**
     * @inheritdoc
     */
    @Override
    public void onReceive(
        Context context,
        Intent intent
    ) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK_NAME);
        wakeLock.acquire(120000);

        Logger.i("Schedule has awoken, starting assistant service.");

        ScheduleManager.startAssistant(context);

        wakeLock.release();
    }

    /**
     * Schedule a new check for app installs.
     * @param context the context to use
     */
    public void scheduleChecks(
        @NonNull Context context
    ) {
        this.cancelChecks(context);

        Logger.i("Scheduling checks");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = ScheduleManager.getPendingIntent(context);
        Schedule checkInterval = PreferenceStorage.getCheckInterval(context);

        try {
            long checkIntervalMilliseconds = Schedule.toMilliseconds(
                    PreferenceStorage.getCheckInterval(context));

            Logger.i("Successfully parsed check interval, using " + checkInterval.toString());

            am.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                checkIntervalMilliseconds,
                pendingIntent);

            Logger.i("New check successfully scheduled in "
                    + checkIntervalMilliseconds / 1000 + " seconds");

        } catch (ParseException ignored) {

        }
    }

    /**
     * Start the assistant.
     */
    public static void startAssistant(
        Context context
    ) {
        if (assistantService == null) {
            assistantService = new AssistantService(context);
        }

        assistantService.startWorker(context, true);
    }

    /**
     * Stop the assistant service, if its running in this context.
     */
    public static void stopAssistant() {
        if (assistantService == null) {
            return;
        }

        assistantService.stop();
    }

    /**
     * Cancel any checks.
     * @param context the context to use
     */
    public void cancelChecks(
        @NonNull Context context
    ) {
        Logger.i("Cancelling checks");

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = ScheduleManager.getPendingIntent(context);

        am.cancel(pendingIntent);
    }

    /**
     * Get a pending intent.
     * @param context the context to use
     * @return the pending intent, used to schedule checks
     */
    private static PendingIntent getPendingIntent(
        @NonNull Context context
    ) {
        Intent intent = new Intent(context, ScheduleManager.class);

        return PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }
}