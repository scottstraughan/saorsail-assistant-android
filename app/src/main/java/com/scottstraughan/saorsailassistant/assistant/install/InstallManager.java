package com.scottstraughan.saorsailassistant.assistant.install;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.logging.Logger;

import java.io.File;

/**
 * Handle the installation of sideload requests.
 */
public final class InstallManager {
    private final Context context;
    private final InstallNotificationManager installNotificationManager;

    /**
     * Constructor.
     */
    public InstallManager(
        Context context
    ) {
        this.context = context;
        this.installNotificationManager = new InstallNotificationManager(context);
    }

    /**
     * Make a sideload request as ready to install.
     */
    public void readyToInstall(
        SideloadRequest sideloadRequest
    ) {
        Logger.i("Installing package " + sideloadRequest.getFriendlyName());

        this.installNotificationManager.showInstallReadyNotification(
            sideloadRequest,
            createInstallApkPendingIntent(this.context, sideloadRequest));
    }

    /**
     * Get an install intent.
     */
    public static Intent createInstallIntent(
        Context context,
        SideloadRequest sideloadRequest
    ) {

        File downloadedFile = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            sideloadRequest.getFileName());

        Uri downloadedFileUri = FileProvider.getUriForFile(
            context,
            "com.scottstraughan.saorsailassistant.FileProvider",
            downloadedFile);

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(
            downloadedFileUri,
            "application/vnd.android.package-archive");

        return installIntent;
    }

    /**
     * Get an install pending intent.
     */
    public static PendingIntent createInstallApkPendingIntent(
        Context context,
        SideloadRequest sideloadRequest
    ) {
        return PendingIntent.getActivity(
            context,
            0,
            createInstallIntent(context, sideloadRequest),
            PendingIntent.FLAG_IMMUTABLE);
    }
}
