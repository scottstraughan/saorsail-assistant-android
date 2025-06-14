package com.scottstraughan.saorsailassistant.assistant.download;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.scottstraughan.saorsailassistant.assistant.AssistantLiveData;
import com.scottstraughan.saorsailassistant.assistant.AssistantState;
import com.scottstraughan.saorsailassistant.assistant.download.monitor.DownloadProgressMonitor;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequestStage;
import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.assistant.install.InstallManager;

import java.io.File;

public final class DownloadManager {
    /**
     * Context to use.
     */
    private final Context context;

    /**
     * Cher live data instance.
     */
    private final AssistantLiveData assistantLiveData;

    /**
     * Download notification manager.
     */
    private final DownloadNotificationManager downloadNotificationManager;

    /**
     * Constructor.
     */
    public DownloadManager(
        Context context
    ) {
        this.context = context;
        this.assistantLiveData = AssistantLiveData.getInstance();
        this.downloadNotificationManager = new DownloadNotificationManager(context);
    }

    /**
     * Download a sideload request.
     */
    public void start(
        SideloadRequest sideloadRequest
    ) {
        Logger.i("Download state: " + sideloadRequest.getStage());

        if (!isInDownloadableState(sideloadRequest)) {
            Logger.i("Skipping download of '" + sideloadRequest.getFriendlyName()
                + "' as it is in an non-downloadable state.");

            return;
        }

        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(
            Uri.parse(sideloadRequest.getAppPackageDownloadUrl()));

        String targetPath = this.getDownloadFilePath(sideloadRequest);

        Logger.i("Downloading: " + sideloadRequest.getFriendlyName() + " to " + targetPath);

        Uri targetUri = Uri.parse("file://" + targetPath);

        request
            .setAllowedNetworkTypes(android.app.DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(sideloadRequest.getFriendlyName())
            .setDescription("Downloading " + sideloadRequest.getFriendlyName())
            .setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_HIDDEN)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationUri(targetUri);

        android.app.DownloadManager downloadManager = (android.app.DownloadManager)
            context.getSystemService(Context.DOWNLOAD_SERVICE);

        long downloadID = downloadManager.enqueue(request);

        DownloadProgressMonitor downloadProgressMonitor = new DownloadProgressMonitor(
            downloadID,
            downloadManager,
            new Handler(Looper.getMainLooper(), msg -> {
                AssistantLiveData
                    .getInstance()
                    .updateDownloadStatus(downloadID, msg.obj);

                if (sideloadRequest.getStage() == SideloadRequestStage.READY_TO_INSTALL) {
                    this.handleCompleted(sideloadRequest);
                }

                return true;
            })
        );

        DownloadReference downloadReference = new DownloadReference(
            downloadID, downloadProgressMonitor);

        downloadProgressMonitor.start();

        sideloadRequest.setStage(SideloadRequestStage.QUEUED);
        sideloadRequest.setDownloadReference(downloadReference);

        this.assistantLiveData.updateSideloadRequest(sideloadRequest);
    }

    /**
     * Start all the downloads.
     */
    public void startAll(
        boolean resetStopped
    ) {
        AssistantState assistantState = this.assistantLiveData.getValue();

        if (resetStopped) {
            Logger.w("Resetting all stopped downloads..");
            this.resetAll();
        }

        for (SideloadRequest sideloadRequest : assistantState.getSideloadRequests()) {
            this.start(sideloadRequest);
        }
    }

    /**
     * Reset all the sideload requests.
     */
    public void resetAll() {
        for (SideloadRequest sideloadRequest
            : this.assistantLiveData.getValue().getSideloadRequests()) {
            this.reset(sideloadRequest);
        }
    }

    /**
     * Reset the state of a sideload request.
     */
    public void reset(
        SideloadRequest sideloadRequest
    ) {
        if (sideloadRequest.getStage() != SideloadRequestStage.STOPPED) {
            return ;
        }

        sideloadRequest.setStage(SideloadRequestStage.QUEUED);
        this.assistantLiveData.updateSideloadRequest(sideloadRequest);
    }

    /**
     * Stop a currently in progress download.
     */
    public void stop(
        SideloadRequest sideloadRequest
    ) {
        if (sideloadRequest.getStage() == SideloadRequestStage.INSTALLED) {
            return;
        } else if (sideloadRequest.getStage() == SideloadRequestStage.READY_TO_INSTALL) {
            return;
        }

        DownloadReference downloadReference = sideloadRequest.getDownloadReference();

        if (downloadReference != null) {
            android.app.DownloadManager downloadManager = (android.app.DownloadManager)
                this.context.getSystemService(Context.DOWNLOAD_SERVICE);

            downloadReference.getDownloadProgressMonitor().stop();

            downloadManager.remove(downloadReference.getDownloadManagerId());

            sideloadRequest.setDownloadReference(null);
        }

        sideloadRequest.setStage(SideloadRequestStage.STOPPED);

        this.assistantLiveData.notifyObservers();
        this.sendDownloadNotification(sideloadRequest);
    }

    /**
     * Stop all downloads.
     */
    public void stopAll() {
        AssistantState checkerState = this.assistantLiveData.getValue();

        for (SideloadRequest sideloadRequest : checkerState.getSideloadRequests()) {
            this.stop(sideloadRequest);
        }
    }

    /**
     * Handle a download marked as completed.
     */
    private void handleCompleted(
        SideloadRequest sideloadRequest
    ) {
        InstallManager installManager = new InstallManager(this.context);
        installManager.readyToInstall(sideloadRequest);
    }

    /**
     * Send a download notification.
     */
    private void sendDownloadNotification(
        SideloadRequest sideloadRequest
    ) {
        this.downloadNotificationManager.send(
            sideloadRequest);
    }

    /**
     * Get the download file path.
     * @param sideloadRequest the installation request
     * @return the target file path.;
     */
    private String getDownloadFilePath(
        SideloadRequest sideloadRequest
    ) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(path, sideloadRequest.getFileName()).getAbsolutePath();
    }

    /**
     * Checks if the sideload request is in a state that we can download.
     */
    private static boolean isInDownloadableState(
        SideloadRequest sideloadRequest
    ) {
        return sideloadRequest.getStage() == SideloadRequestStage.QUEUED
            || sideloadRequest.getStage() == SideloadRequestStage.STOPPED;
    }
}
