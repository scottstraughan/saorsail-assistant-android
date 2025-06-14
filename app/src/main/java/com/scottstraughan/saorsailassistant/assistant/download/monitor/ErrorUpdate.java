package com.scottstraughan.saorsailassistant.assistant.download.monitor;

public class ErrorUpdate {
    private final long errorCode;

    public ErrorUpdate(
        long errorCode
    ) {
        this.errorCode = errorCode;
    }

    public String getStatusMessage() {
        switch ((int) this.errorCode) {
            case android.app.DownloadManager.ERROR_CANNOT_RESUME:
                return "ERROR_CANNOT_RESUME";
            case android.app.DownloadManager.ERROR_DEVICE_NOT_FOUND:
                return "ERROR_DEVICE_NOT_FOUND";
            case android.app.DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                return "ERROR_FILE_ALREADY_EXISTS";
            case android.app.DownloadManager.ERROR_FILE_ERROR:
                return "ERROR_FILE_ERROR";
            case android.app.DownloadManager.ERROR_HTTP_DATA_ERROR:
                return "ERROR_HTTP_DATA_ERROR";
            case android.app.DownloadManager.ERROR_INSUFFICIENT_SPACE:
                return "ERROR_INSUFFICIENT_SPACE";
            case android.app.DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                return "ERROR_TOO_MANY_REDIRECTS";
            case android.app.DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                return "ERROR_UNHANDLED_HTTP_CODE";
            case android.app.DownloadManager.ERROR_UNKNOWN:
                return "ERROR_UNKNOWN";

            // HTTP Errors
            case 404:
                return "APK Not Found (404)";
            case 401:
                return "Permission Denied (401)";
        }

        return "Unknown Error, Code: " + errorCode;
    }
}