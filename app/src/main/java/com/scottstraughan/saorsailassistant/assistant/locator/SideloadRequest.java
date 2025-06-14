package com.scottstraughan.saorsailassistant.assistant.locator;

import android.webkit.URLUtil;

import androidx.annotation.Nullable;

import com.scottstraughan.saorsailassistant.assistant.download.DownloadReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Represents a sideload request. A sideload request is represents a APK/Package that should be
 * downloaded and installed on the current device.
 */
public class SideloadRequest {
    private static long _ID = 0;

    public long id = SideloadRequest._ID;
    private final String namespace;
    private final String iconUrl;
    private final String appName;
    private final String appPackageDownloadUrl;

    private DownloadReference downloadReference = null;

    public SideloadRequestStage stage = SideloadRequestStage.QUEUED;


    /**
     * Construct a new InstallRequest.
     */
    public SideloadRequest(
        String namespace,
        String iconUrl,
        String appName,
        String appPackageDownloadUrl
    ) {
        this.namespace = namespace;
        this.iconUrl = iconUrl;
        this.appName = appName;
        this.appPackageDownloadUrl = appPackageDownloadUrl;

        SideloadRequest._ID += 1;
    }

    /**
     * Get a status message to show to a user.
     */
    public String getStatusMessage() {
        switch (this.stage) {
            case QUEUED:
                return "Queued";
            case INSTALLED:
                return "Installed";
            case STOPPED:
                return "Stopped";
            case READY_TO_INSTALL:
                return "Ready to install";
            case DOWNLOADING:
                if (this.downloadReference != null) {
                    return this.downloadReference.getStatusMessage();
                }

                return "Pending";
            case DOWNLOAD_ERROR:
                if (this.downloadReference != null) {
                    return this.downloadReference.getStatusMessage();
                }

                return "Download Error";
        }

        return "Queued";
    }

    /**
     * Return a unique id for this pending install request.
     * @return the unique id
     */
    public long getId() {
        return this.id;
    }

    /**
     * The icon url
     * @return url
     */
    public String getIconUrl() {
        return this.iconUrl;
    }

    /**
     * The app namespace in format com.name.package
     * @return namespace
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Get the current stage of the install request.
     * @return the status
     */
    public SideloadRequestStage getStage() {
        return this.stage;
    }

    public boolean isDoingSomething() {
        if (this.getStage() == SideloadRequestStage.QUEUED) {
            return true;
        } else return this.getStage() == SideloadRequestStage.DOWNLOADING;
    }

    /**
     * Set the stage.
     */
    public void setStage(
        SideloadRequestStage stage
    ) {
        this.stage = stage;
    }

    /**
     * Get a friendly name for the install request.
     * @return the friendly name
     */
    public String getFriendlyName() {
        return this.appName;
    }

    /**
     * Get the package name.
     * @return package name
     */
    public String getFileName() {
        return URLUtil.guessFileName(this.appPackageDownloadUrl, null, null);
    }

    /**
     * Set the download reference
     * @param downloadReference the reference to set
     */
    public void setDownloadReference(
        @Nullable DownloadReference downloadReference
    ) {
        this.downloadReference = downloadReference;
    }

    /**
     * Get the app download url.
     */
    public String getAppPackageDownloadUrl() {
        return this.appPackageDownloadUrl;
    }

    /**
     * Get the download reference if it exists.
     */
    @Nullable
    public DownloadReference getDownloadReference()  {
        return this.downloadReference;
    }

    /**
     * Convert a JSONObject into a InstallRequest.
     * @param object the JSONObject
     * @return created InstallRequest
     * @throws JSONException if there was an error converting
     */
    public static SideloadRequest FromJSONObject(
        JSONObject object
    ) throws JSONException {
        String namespace = object.get("appNamespace").toString();
        String iconUrl = object.get("appIconUrl").toString();
        String appName = object.get("appName").toString();
        String appPackageDownloadUrl = object.get("appPackageDownloadUrl").toString();

        return new SideloadRequest(
            namespace,
            iconUrl,
            appName,
            appPackageDownloadUrl);
    }

    /**
     * Convert a JSONArray into an ArrayList of InstallRequest.
     * @param arrayOfObjects the list of JSONObject
     * @return a list of InstallRequest
     * @throws JSONException if there was a problem converting
     */
    public static ArrayList<SideloadRequest> FromJSONArray(
        JSONArray arrayOfObjects
    ) throws JSONException {
        ArrayList<SideloadRequest> requests = new ArrayList<>();

        for (int i = 0; i < arrayOfObjects.length(); i++) {
            requests.add(SideloadRequest.FromJSONObject(arrayOfObjects.getJSONObject(i)));
        }

        return requests;
    }
}