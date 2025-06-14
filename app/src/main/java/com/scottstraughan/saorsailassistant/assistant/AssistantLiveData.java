package com.scottstraughan.saorsailassistant.assistant;

import androidx.lifecycle.LiveData;

import com.scottstraughan.saorsailassistant.assistant.download.DownloadReference;
import com.scottstraughan.saorsailassistant.assistant.download.DownloadStatus;
import com.scottstraughan.saorsailassistant.assistant.download.monitor.ProgressUpdate;
import com.scottstraughan.saorsailassistant.assistant.download.monitor.ErrorUpdate;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequestStage;
import com.scottstraughan.saorsailassistant.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Facilitates observers to track the internal state of the assistant.
 */
public class AssistantLiveData extends LiveData<AssistantState> {
    /**
     * Singleton instance.
     */
    private static final AssistantLiveData instance = new AssistantLiveData();

    /**
     * The checker state reference.
     */
    private final AssistantState assistantState
        = new AssistantState();

    /**
     * Private Constructor since we want to ensure getInstance() is used.
     */
    private AssistantLiveData() { }

    /**
     * @inheritdoc
     */
    @NotNull
    @Override
    public AssistantState getValue() {
        return this.assistantState;
    }

    /**
     * Get an instance of the live data.
     * @return instance
     */
    public static synchronized AssistantLiveData getInstance() {
        return instance;
    }

    /**
     * Add a list of sideload requests.
     */
    public synchronized void addSideloadRequests(
        ArrayList<SideloadRequest> sideloadRequests
    ) {
        for (SideloadRequest sideloadRequest : sideloadRequests) {
            this.addSideloadRequest(sideloadRequest);
        }

        this.notifyObservers();
    }

    /**
     * Update a sideload request internally and notify any observers.
     */
    public synchronized void updateSideloadRequest(
        SideloadRequest sideloadRequest
    ) {
        this.assistantState.addSideloadRequest(sideloadRequest);
        this.notifyObservers();
    }

    /**
     * Check if there are slide requests.
     */
    public synchronized boolean hasItems() {
        return !this.assistantState.getSideloadRequests().isEmpty();
    }

    /**
     * Update the download progress of a sideload request.
     */
    public synchronized void updateDownloadStatus(
        long downloadId,
        Object status
    ) {
        SideloadRequest sideloadRequest = this.getSideloadRequest(downloadId);

        if (sideloadRequest == null) {
            return;
        }

        DownloadReference downloadReference = sideloadRequest.getDownloadReference();

        if (downloadReference == null) {
            return;
        }

        sideloadRequest.setStage(SideloadRequestStage.DOWNLOADING);

        if (status instanceof ProgressUpdate) {
            ProgressUpdate downloadingStatus = (ProgressUpdate) status;
            downloadReference.setProgressPercentage(downloadingStatus.getProgressPercentage());

            if (downloadingStatus.isComplete()) {
                sideloadRequest.setStage(SideloadRequestStage.READY_TO_INSTALL);
                downloadReference.setStatus(DownloadStatus.COMPLETED);
            } else if (!downloadingStatus.isStarted()) {
                downloadReference.setStatus(DownloadStatus.PENDING);
            } else {
                downloadReference.setStatus(DownloadStatus.DOWNLOADING);
            }
        } else if (status instanceof ErrorUpdate) {
            sideloadRequest.setStage(SideloadRequestStage.DOWNLOAD_ERROR);
            downloadReference.setStatus(DownloadStatus.FAILED);

            ErrorUpdate errorUpdate = (ErrorUpdate) status;
            downloadReference.setErrorStatus(errorUpdate);
        }

        Logger.i("Sideloare REquest state:" + sideloadRequest.getStage());

        this.notifyObservers();
    }

    @Nullable
    public SideloadRequest getSideloadRequest(
        long downloadId
    ) {
        for (SideloadRequest sideloadRequest : this.assistantState.getSideloadRequests()) {
            DownloadReference downloadReference = sideloadRequest.getDownloadReference();

            if (downloadReference == null) {
                continue;
            }

            if (downloadReference.getDownloadManagerId() == downloadId) {
                return sideloadRequest;
            }
        }

        return null;
    }

    /**
     * Add a sideload request.
     */
    public synchronized void addSideloadRequest(
        SideloadRequest sideloadRequest
    ) {
        if (this.assistantState.hasSideloadRequests(sideloadRequest)) {
            return;
        }

        this.assistantState.addSideloadRequest(sideloadRequest);

        this.notifyObservers();
    }

    /**
     * Set the global running state and notify observers.
     */
    public synchronized void setRunning(
        boolean running
    ) {
        this.assistantState.setRunning(running);
        this.notifyObservers();
    }

    /**
     * Notify any observers of changes.
     */
    public synchronized void notifyObservers() {
        this.postValue(this.assistantState);
    }
}
