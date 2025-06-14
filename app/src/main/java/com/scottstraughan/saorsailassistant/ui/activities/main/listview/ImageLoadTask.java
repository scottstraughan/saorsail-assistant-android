package com.scottstraughan.saorsailassistant.ui.activities.main.listview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.scottstraughan.saorsailassistant.logging.Logger;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
    public interface ImageLoadedCallback {
        void onIconLoaded(Bitmap iconBitmap);
    }

    private final String url;
    private final ImageLoadedCallback callback;

    public ImageLoadTask(
        String url,
        ImageLoadedCallback callback
    ) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(
        Void... params
    ) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Logger.e("Unable to load image " + e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(
        Bitmap result
    ) {
        super.onPostExecute(result);
        this.callback.onIconLoaded(result);
    }
}
