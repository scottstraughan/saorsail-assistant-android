package com.scottstraughan.saorsailassistant.assistant.install;

import android.app.PendingIntent;
import android.content.Context;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.notifications.Notification;
import com.scottstraughan.saorsailassistant.notifications.NotificationChannelType;
import com.scottstraughan.saorsailassistant.notifications.NotificationManager;

/**
 * Notification manager that handles install related notifications.
 */
public class InstallNotificationManager extends NotificationManager {
    /**
     * Constructor.
     */
    public InstallNotificationManager(
        Context context
    ) {
        super(context, NotificationChannelType.INSTALLS);
    }

    /**
     * Show an install ready notification.
     * @param sideloadRequest the sideload request
     * @param installApkIntent the pending intent to fire when the notification is clicked
     */
    public void showInstallReadyNotification(
        SideloadRequest sideloadRequest,
        PendingIntent installApkIntent
    ) {
        Notification notification = new Notification();
        notification.title = sideloadRequest.getFriendlyName() + " is ready to install";
        notification.content = "Click here to install "
            + sideloadRequest.getFriendlyName() + " on your device";
        notification.action = installApkIntent;
        notification.autoCancel = true;

        this.show(notification);
    }
}
