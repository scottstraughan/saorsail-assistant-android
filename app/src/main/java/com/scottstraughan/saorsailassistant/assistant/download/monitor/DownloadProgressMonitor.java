package com.scottstraughan.saorsailassistant.assistant.download.monitor;

import android.app.DownloadManager;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;

import com.scottstraughan.saorsailassistant.logging.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Monitors the progress of a in progress download.
 */
public final class DownloadProgressMonitor {
    /**
     * Unique download id.
     */
    private final long downloadId;

    /**
     * Download manager.
     */
    private final DownloadManager downloadManager;

    /**
     * Handler callback.
     */
    private final Handler progressHandler;

    /**
     * If we are completed or not.
     */
    private boolean completed = false;

    /**
     * Constructor.
     * @param downloadId the unique id of the download
     * @param downloadManager the download manager to use
     * @param progressHandler provided progress handler
     */
    public DownloadProgressMonitor(
            long downloadId,
            DownloadManager downloadManager,
            Handler progressHandler
    ) {
        this.downloadId = downloadId;
        this.downloadManager = downloadManager;
        this.progressHandler = progressHandler;
    }

    /**
     * Start a progress monitor.
     */
    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        executor.execute(() -> {
            while (!completed) {
                Cursor cursor = this.downloadManager.query(
                    new DownloadManager.Query().setFilterById(this.downloadId));

                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int downloadStatus = cursor.getInt(columnIndex);

                    Message message = Message.obtain();

                    switch (downloadStatus) {
                        case DownloadManager.STATUS_RUNNING:
                            columnIndex = cursor.getColumnIndex(
                                DownloadManager.COLUMN_TOTAL_SIZE_BYTES);

                            long totalBytes = cursor.getLong(columnIndex);

                            if (totalBytes > 0) {
                                columnIndex = cursor.getColumnIndex(
                                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);

                                int progress =
                                    (int) (cursor.getLong(columnIndex) * 100 / totalBytes);

                                message.obj = new ProgressUpdate(progress);
                            }

                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            message.obj = new ProgressUpdate(100);
                            completed = true;
                            break;
                        case DownloadManager.STATUS_FAILED:
                            int errorIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                            long errorCode = cursor.getLong(errorIndex);

                            message.obj = new ErrorUpdate(errorCode);
                            completed = true;

                            Logger.e("Progress monitor found an error, exiting.");
                            break;
                    }

                    progressHandler.sendMessage(message);
                }

                if (completed) {
                    Logger.i("Progress monitor completed.");
                }

                cursor.close();
            }
        });
    }

    /**
     * Stop and tidy the progress monitor.
     */
    public void stop() {
        Logger.i("Stopping progress monitor.");

        this.completed = true;
        this.progressHandler.removeCallbacksAndMessages(null);
    }
}