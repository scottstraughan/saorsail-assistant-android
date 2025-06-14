package com.scottstraughan.saorsailassistant.assistant.download;

import android.content.Context;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequestStage;
import com.scottstraughan.saorsailassistant.notifications.Notification;
import com.scottstraughan.saorsailassistant.notifications.NotificationChannelType;
import com.scottstraughan.saorsailassistant.notifications.NotificationManager;

import java.util.HashMap;

public class DownloadNotificationManager extends NotificationManager {
    /**
     * Map of all created notifications.
     */
    private final HashMap<Long, Notification> notifications = new HashMap<>();

    /**
     * Constructor.
     */
    public DownloadNotificationManager(
        Context context
    ) {
        super(context, NotificationChannelType.DOWNLOADS);
    }

    /**
     * Send a download notification, creating one if it doesn't already exist and updating if it
     * does.
     */
    public void send(
        SideloadRequest sideloadRequest
    ) {
        Notification notification = this.notifications.get(sideloadRequest.getId());

        if (notification == null) {
            notification = this.create(sideloadRequest);
        }

        DownloadReference downloadReference
            = sideloadRequest.getDownloadReference();

        notification.title = sideloadRequest.getFriendlyName() + " is queued";
        notification.progress = 0;

        if (downloadReference != null) {
            notification.title = downloadReference.getStatusMessage();
            notification.progress = downloadReference.getProgressPercentage();
        }

        if (this.shouldBeShowing(sideloadRequest)) {
            this.show(notification);
        } else {
            this.close(sideloadRequest, notification);
        }
    }

    /**
     * Close/hide a download notification.
     */
    private void close(
        SideloadRequest sideloadRequest,
        Notification notification
    ) {
        this.notifications.remove(sideloadRequest.getId());
        this.close(notification);
    }

    /**
     * Create a new download notification, adding it to the map of tracked notifications.
     */
    private Notification create(
        SideloadRequest sideloadRequest
    ) {
        Notification notification = new Notification();
        notification.title = sideloadRequest.getFriendlyName() + " is downloading";
        notification.content = sideloadRequest.getFriendlyName() + " has started " +
            "downloading and will be available to install shortly";
        notification.progressMax = 100;
        notification.progress = 0;

        notifications.put(sideloadRequest.getId(), notification);

        return notification;
    }

    /**
     * Check if the sideload request is in a state that should be showing a notification.
     */
    private boolean shouldBeShowing(
        SideloadRequest sideloadRequest
    ) {
        return sideloadRequest.getStage() == SideloadRequestStage.DOWNLOADING;
    }
}