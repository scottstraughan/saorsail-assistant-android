package com.scottstraughan.saorsailassistant.notifications;

import android.app.PendingIntent;

import androidx.core.app.NotificationCompat;

import com.scottstraughan.saorsailassistant.R;

/**
 * Notification information wrapper.
 */
public class Notification {
    public static int _id = 0;

    /**
     * Unique id for the notification.
     */
    private final int id;

    /**
     * Icon to use.
     */
    public int icon = R.drawable.ic_launcher_foreground;

    /**
     * Visibility of the notification.
     */
    public int visibility = NotificationCompat.VISIBILITY_PUBLIC;

    /**
     * Priority of the notification.
     */
    public int priority = NotificationCompat.PRIORITY_DEFAULT;

    /**
     * If true, the notification will close when the user clicks on it.
     */
    public boolean autoCancel = false;

    public String title;
    public String content;
    public PendingIntent action;

    public int progressMax = 0;
    public int progress = 0;

    /**
     * Constructor.
     */
    public Notification() {
        this.id = _id;
        _id = _id + 1;
    }

    /**
     * @return the id of the notification
     */
    public int getId() {
        return this.id;
    }

    /**
     * Determines if this notification has a progress view assigned to it.
     * @return true if it has
     */
    public boolean shouldShowProgressBar() {
        return this.progressMax != 0 || this.progress == this.progressMax;
    }
}
