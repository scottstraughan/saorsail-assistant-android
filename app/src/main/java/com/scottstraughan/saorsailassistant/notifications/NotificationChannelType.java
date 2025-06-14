package com.scottstraughan.saorsailassistant.notifications;

import android.app.NotificationManager;

/**
 * Notification channels.
 */
public enum NotificationChannelType {
    DOWNLOADS,
    INSTALLS;

    /**
     * Get a channel name.
     * @param notificationChannelType type of chanel
     * @return channel name
     */
    public static String getName(NotificationChannelType notificationChannelType)  {
        if (notificationChannelType == DOWNLOADS) {
            return "Download Notifications";
        } else if (notificationChannelType == INSTALLS) {
            return "Install Notifications";
        }

        return "Unknown";
    }

    /**
     * Get a channel description.
     * @param notificationChannelType the channel type
     * @return description of  channel
     */
    public static String getDescription(NotificationChannelType notificationChannelType)  {
        if (notificationChannelType == DOWNLOADS) {
            return "Notifications for when downloads start and complete";
        } else if (notificationChannelType == INSTALLS) {
            return "Notifications for when apps are ready to be installed";
        }

        return "Unknown";
    }

    /**
     * Get importance for a channel.
     * @param notificationChannelType type of chanel
     * @return channel importance
     */
    public static int getImportance(NotificationChannelType notificationChannelType)  {
        if (notificationChannelType == DOWNLOADS) {
            return android.app.NotificationManager.IMPORTANCE_DEFAULT;
        } else if (notificationChannelType == INSTALLS) {
            return NotificationManager.IMPORTANCE_HIGH;
        }

        return android.app.NotificationManager.IMPORTANCE_DEFAULT;
    }
}
