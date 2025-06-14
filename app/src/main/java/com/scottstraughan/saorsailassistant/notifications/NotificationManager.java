package com.scottstraughan.saorsailassistant.notifications;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.scottstraughan.saorsailassistant.logging.Logger;

/**
 * Notification manager.
 */
public class NotificationManager  {
    protected final Context context;
    protected final NotificationChannelType notificationChannelType;

    /**
     * Create a new notification manager.
     * @param context context to use
     * @param notificationChannelType channel type
     */
    protected NotificationManager(
        Context context,
        NotificationChannelType notificationChannelType
    ) {
        this.context = context;
        this.notificationChannelType = notificationChannelType;
    }

    /**
     * Show a notification.
     * @param notification the notification to show.
     */
    protected void show(
        Notification notification
    ) {
        NotificationCompat.Builder builder = this.build(notification);

        if (ActivityCompat.checkSelfPermission(this.context, POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat nm = NotificationManagerCompat.from(this.context);
            nm.notify(notification.getId(), builder.getNotification());
        } else {
            Logger.e("Cannot show notification, permission denied.");
        }
    }

    /**
     * Close a showing notification.
     */
    protected void close(
        Notification notification
    ) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(this.context);
        nm.cancel(notification.getId());
    }

    /**
     * Build a notification.
     * @param notification notification to build
     * @return the built notification builder
     */
    protected NotificationCompat.Builder build(
        Notification notification
    ) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
            this.context,
            this.notificationChannelType.toString())
            .setSmallIcon(notification.icon)
            .setContentTitle(notification.title)
            .setContentText(notification.content)
            .setVisibility(notification.visibility)
            .setOnlyAlertOnce(false)
            .setPriority(notification.priority)
            .setAutoCancel(notification.autoCancel)
            .setContentIntent(notification.action);

        if (notification.shouldShowProgressBar()) {
            builder.setOnlyAlertOnce(true);
            builder.setProgress(
                notification.progressMax, notification.progress, false);
        }

        return builder;
    }

    /**
     * Update an existing notification.
     * @param notification the notification to update.
     */
    public void update(
        Notification notification
    ) {
        this.show(notification);
    }

    /**
     * Check if notifications are enabled or not.
     * @return true or false
     */
    public static boolean isAllowed(
        Context context
    ) {
        return context.getSystemService(
                android.app.NotificationManager.class).areNotificationsEnabled();
    }

    /**
     * Ask permission for notifications
     * @param context context to us
     */
    public static void askPermission(
        Context context
    ) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("app_package", context.getPackageName());
        intent.putExtra("app_uid", context.getApplicationInfo().uid);
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

        context.startActivity(intent);
    }

    /**
     * Create a notification channel.
     */
    @SuppressLint("ObsoleteSdkInt")
    public static void registerChannel(
        Context context,
        NotificationChannelType notificationChannelType
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationChannelType.getImportance(notificationChannelType);

            NotificationChannel notificationChannel = new NotificationChannel(
                    notificationChannelType.toString(),
                    NotificationChannelType.getName(notificationChannelType),
                    importance);

            notificationChannel.setDescription(
                    NotificationChannelType.getDescription(notificationChannelType));

            android.app.NotificationManager notificationManager = context.getSystemService(
                    android.app.NotificationManager.class);

            notificationManager.createNotificationChannel(notificationChannel);

            Logger.i("Registered notification channel.");
        }
    }

    /**
     * Register all the required notification channels. We need to do this early so we can have
     * our channels available in the apps settings.
     * @param context context to use.
     */
    public static void registerNotificationChannels(Context context) {
        NotificationManager.registerChannel(
            context, NotificationChannelType.DOWNLOADS);

        NotificationManager.registerChannel(
            context, NotificationChannelType.INSTALLS);
    }
}
