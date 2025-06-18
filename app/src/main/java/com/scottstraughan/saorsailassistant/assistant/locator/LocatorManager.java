package com.scottstraughan.saorsailassistant.assistant.locator;

import android.content.Context;

import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.storage.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Locates any sideload requests from the remote server.
 */
public final class LocatorManager {
    /**
     * Context to use.
     */
    private final Context context;

    /**
     * Constructor.
     */
    public LocatorManager(
        Context context
    ) {
        this.context = context;
    }

    /**
     * Check for sideload requests.
     */
    public ArrayList<SideloadRequest> locateSideloadRequests() {
        ArrayList<SideloadRequest> newInstallRequests = new ArrayList<>();

        try {
            Logger.i("Checking for sideload requests...");

            String pairCode = PreferenceStorage.getPairCode(context);

            newInstallRequests.addAll(
                this.fetchSideloadRequestsFromApi(pairCode));

            Logger.i("Found " + newInstallRequests.size() + " sideload requests.");
        } catch (Exception e) {
            Logger.e(e.toString());
        }

        return newInstallRequests;
    }

    /**
     * Get a list of sideload requests from the API.
     */
    private ArrayList<SideloadRequest> fetchSideloadRequestsFromApi(
        String pairCode
    ) throws JSONException {
        String responseStream = "";
        String locatorUrlEndpoint = PreferenceStorage.getAppInstallCheckUrl(this.context)
            + "/check/";

        try {
            URL url = new URL(locatorUrlEndpoint + pairCode);
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

            Logger.i("Checking for sideload requests are URL: " + url);

            // Check the connection status
            if (urlConnection.getResponseCode() == 200) {
                // if response code = 200 ok
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Read the BufferedInputStream
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }

                responseStream = sb.toString();
                urlConnection.disconnect();

                Logger.i("Sideload API returned: " + responseStream);
            }
        } catch (IOException e) {
            Logger.e(e.toString());
            return new ArrayList<>();
        }

        return SideloadRequest.FromJSONArray(
            new JSONArray(responseStream));
    }
}
