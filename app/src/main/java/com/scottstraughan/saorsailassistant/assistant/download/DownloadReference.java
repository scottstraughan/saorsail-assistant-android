package com.scottstraughan.saorsailassistant.assistant.download;

import com.scottstraughan.saorsailassistant.assistant.download.monitor.DownloadProgressMonitor;
import com.scottstraughan.saorsailassistant.assistant.download.monitor.ErrorUpdate;

/**
 * Represents a downloading item, contains information related to the DownloadManager.
 */
public class DownloadReference {
    private final long downloadManagerId;
    private final DownloadProgressMonitor downloadProgressMonitor;
    private int progressPercentage = 0;
    private ErrorUpdate errorUpdate;

    private DownloadStatus status = DownloadStatus.PENDING;

    /**
     * Constructor.
     */
    public DownloadReference(
        long id,
        DownloadProgressMonitor downloadProgressMonitor
    ) {
        this.downloadManagerId = id;
        this.downloadProgressMonitor = downloadProgressMonitor;
    }

    public void setStatus(
        DownloadStatus status
    ) {
        this.status = status;
    }

    public DownloadStatus getStatus() {
        return this.status;
    }

    /**
     * Return the unique id for this download, used for the DownloadManager.
     */
    public long getDownloadManagerId() {
        return this.downloadManagerId;
    }

    /**
     * Get the download progress monitor.
     */
    public DownloadProgressMonitor getDownloadProgressMonitor() {
        return this.downloadProgressMonitor;
    }

    public void setErrorStatus(
        ErrorUpdate errorUpdate
    ) {
        this.errorUpdate = errorUpdate;
    }

    public void setProgressPercentage(
        int progressPercentage
    ) {
        this.progressPercentage = progressPercentage;
    }

    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    public String getStatusMessage() {
        switch (this.status) {
            case DOWNLOADING:
                return "Downloading " + this.progressPercentage + "%";
            case COMPLETED:
                return "Download Completed";
            case FAILED:
                if (this.errorUpdate != null) {
                    return this.errorUpdate.getStatusMessage();
                }

                return "Error";
            case PENDING:
                return "Download is Pending";
            case STOPPED:
                return "Download is Stopped";
        }

        return "Download is Pending";
    }
}
