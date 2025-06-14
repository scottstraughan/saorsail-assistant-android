package com.scottstraughan.saorsailassistant.assistant;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.scheduler.ScheduleManager;
import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.storage.PreferenceStorage;
import com.scottstraughan.saorsailassistant.assistant.download.DownloadManager;
import com.scottstraughan.saorsailassistant.assistant.locator.LocatorManager;

import java.util.ArrayList;

/**
 * The core assistant service, performs all the backend work required to make this app work. The
 * assistant service does the following:
 * <ul>
 *     <li>Checks for new sideload requests using the API</li>
 *     <li>Schedule a download of the request application package</li>
 *     <li>When the download is complete, prepares the application for installation</li>
 * </ul>
 */
public final class AssistantService
    extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Sideload Request Locator Manager
     */
    private final LocatorManager locatorManager;

    /**
     * Package Download Manager
     */
    private final DownloadManager downloadManager;

    /**
     * Schedule manager for periodic assistant runs
     */
    private final ScheduleManager scheduleManager;

    /**
     * A reference the the worker thread, can be null if there is none.
     */
    private Thread workerThread;

    /**
     * Default constructor, used when called as a service.
     */
    public AssistantService() {
        this.locatorManager = new LocatorManager(this);
        this.downloadManager = new DownloadManager(this);
        this.scheduleManager = new ScheduleManager();
    }

    /**
     * Constructor, used when called directly.
     */
    public AssistantService(
        Context context
    ) {
        this.locatorManager = new LocatorManager(context);
        this.downloadManager = new DownloadManager(context);
        this.scheduleManager = new ScheduleManager();
    }

    /**
     * Called when the service is started.
     */
    @Override
    public void onStart(
        Intent intent,
        int startId
    ) {
        super.onStart(intent, startId);

        boolean resetStopped = false;

        if (intent != null) {
            resetStopped = intent.getBooleanExtra("resetStopped", false);
        }

        PreferenceManager
            .getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this);

        Logger.i("Starting assistant service.");

        this.startWorker(this, resetStopped);
    }

    /**
     * Called when the service is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        this.stop();

        PreferenceManager
            .getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this);

        Logger.w("Stopping assistant service.");
    }

    /**
     * Bind to this service.
     */
    @Nullable
    @Override
    public IBinder onBind(
        Intent intent
    ) {
        return null;
    }

    /**
     * Monitor any changes to preferences, so we can be ensured we only run if we are allowed to
     * and we are in a correct state.
     */
    @Override
    public void onSharedPreferenceChanged(
        SharedPreferences sharedPreferences,
        @Nullable String key
    ) {
        this.restartWorker(this);
    }

    /**
     * Stop and then start the worker.
     */
    private void restartWorker(
        Context context
    ) {
        this.stop();
        this.startWorker(context, false);
    }

    /**
     * Start the worker.
     */
    public synchronized void startWorker(
        Context context,
        boolean resetStopped
    ) {
        if (!PreferenceStorage.isAssistantEnabled(context)) {
            Logger.w("Cannot start, assistant is disabled.");
            return;
        }

        if (!PreferenceStorage.isPairCodeValid(context)) {
            Logger.w("Cannot start, pair code is invalid.");
            return;
        }

        if (this.workerThread != null) {
            Logger.w("Thread already running, continuing...");
            return;
        }

        this.workerThread = new Thread(() -> {
            Logger.i("Thread started.");

            AssistantLiveData checkerLiveData = AssistantLiveData.getInstance();

            // Set the global running state
            checkerLiveData.setRunning(true);

            try {
                Logger.i("Working....");

                // Get all the sideload requests
                ArrayList<SideloadRequest> sideloadRequests =
                    this.locatorManager.locateSideloadRequests();

                // Add all the sideload requests to the internal state
                checkerLiveData.addSideloadRequests(sideloadRequests);

                // Notify download manage to start downloading any pending items
                this.downloadManager.startAll(resetStopped);

                // Schedule next assistant run
                this.scheduleManager.scheduleChecks(context);

                // Mark as stopped
                this.stopped();

            } catch (Exception e) {
                Logger.w("Thread stopped.");
                this.stopped();
            }
        });

        this.workerThread.start();
    }

    /**
     * Stop the worker.
     */
    public synchronized void stop() {
        Logger.w("Stop called on AssistantService, stopping everything..");

        // Ensure all downloads stop in their tracks
        this.downloadManager.stopAll();

        if (this.workerThread == null) {
            return;
        }

        this.workerThread.interrupt();

        this.stopped();
    }

    /**
     * Called when fully stopped.
     */
    private synchronized void stopped() {
        this.workerThread = null;

        // Set the global running state
        AssistantLiveData.getInstance().setRunning(false);

        Logger.i("Assistant service is now stopped.");
    }
}
