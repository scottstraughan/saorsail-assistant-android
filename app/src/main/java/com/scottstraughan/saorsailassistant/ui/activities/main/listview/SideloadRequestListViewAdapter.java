package com.scottstraughan.saorsailassistant.ui.activities.main.listview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.assistant.download.DownloadReference;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequestStage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SideloadRequestListViewAdapter extends BaseAdapter {
    private final HashMap<Integer, SideloadRequestListItem> downloadListItemHashMap = new HashMap<>();

    private final Context context;

    public SideloadRequestListViewAdapter(
        @NonNull Context context
    ) {
        this.context = context;
    }

    private Context getContext() {
        return this.context;
    }

    public void setItems(
        ArrayList<SideloadRequest> sideloadRequests
    ) {
        for (SideloadRequest sideloadRequest : sideloadRequests) {
            this.setItem(sideloadRequest);
        }

        this.notifyDataSetChanged();
    }

    public int getPosition(
        SideloadRequestListItem sideloadRequestListItem
    ) {
        for (Map.Entry<Integer, SideloadRequestListItem> entry : this.downloadListItemHashMap.entrySet()) {
            SideloadRequestListItem current = entry.getValue();

            if (current.getId() == sideloadRequestListItem.getId()) {
                return entry.getKey();
            }
        }

        return -1;
    }

    private void setItem(
        SideloadRequest sideloadRequest
    ) {
        SideloadRequestListItem sideloadRequestListItem = new SideloadRequestListItem(sideloadRequest);

        int existingIndex = this.getPosition(sideloadRequestListItem);

        if (existingIndex == -1) {
            existingIndex = this.downloadListItemHashMap.size();
        }

        this.downloadListItemHashMap.put(existingIndex, sideloadRequestListItem);
    }

    @Override
    public int getCount() {
        return this.downloadListItemHashMap.size();
    }

    @Override
    public SideloadRequestListItem getItem(int position) {
        return this.downloadListItemHashMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Objects.requireNonNull(this.downloadListItemHashMap.get(position)).getId();
    }

    @NonNull
    @Override
    public View getView(
        int position,
        @Nullable View convertView,
        @NonNull ViewGroup parent
    ) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(
                getContext()).inflate(R.layout.sideload_request_list_item, parent, false);
        }

        SideloadRequestListItem sideloadRequestListItem = getItem(position);

        if (sideloadRequestListItem == null) {
            return currentItemView;
        }

        ImageView iconView = currentItemView.findViewById(R.id.installRequestAppIcon);

        new ImageLoadTask(
            sideloadRequestListItem.getIcon(), iconView::setImageBitmap)
            .execute();

        TextView titleTextView = currentItemView.findViewById(R.id.installRequestAppTitle);
        titleTextView.setText(sideloadRequestListItem.getTitle());

        TextView statusTextView = currentItemView.findViewById(R.id.installRequestAppStatus);
        statusTextView.setText(sideloadRequestListItem.getStatusMessage());

        ImageView statusIcon = currentItemView.findViewById(R.id.statusIcon);
        CircularProgressIndicator circularProgressIndicator =
            currentItemView.findViewById(R.id.downloadItemProgressIndicator);

        SideloadRequest sideloadRequest = sideloadRequestListItem
            .getSideloadRequest();

        SideloadRequestStage sideloadRequestStage = sideloadRequest.getStage();

        // Default is to show icon and hide progress indicator
        circularProgressIndicator.setVisibility(View.GONE);
        statusIcon.setVisibility(View.VISIBLE);
        statusIcon.setColorFilter(null);

        // Set the icon color
        statusIcon.setColorFilter(
            ContextCompat.getColor(context, getIconColor(sideloadRequestStage)),
            PorterDuff.Mode.SRC_IN);

        // Set the status icon
        statusIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                this.context, getStatusIcon(sideloadRequestStage)));

        DownloadReference downloadReference = sideloadRequest.getDownloadReference();

        if (downloadReference != null
            && sideloadRequestStage == SideloadRequestStage.DOWNLOADING) {
            statusIcon.setVisibility(View.GONE);
            circularProgressIndicator.setVisibility(View.VISIBLE);
            circularProgressIndicator.setProgress(downloadReference.getProgressPercentage());
        }

        return currentItemView;
    }

    public static int getIconColor(
        SideloadRequestStage stage
    ) {
        switch (stage) {
            case DOWNLOAD_ERROR:
                return R.color.red;
            case INSTALLED:
                return R.color.blue;
        }

        return R.color.blue;
    }

    public static int getStatusIcon(
        SideloadRequestStage stage
    ) {
        switch (stage) {
            case QUEUED:
                return R.drawable.queued;
            case DOWNLOADING:
                return R.drawable.queued;
            case DOWNLOAD_ERROR:
                return R.drawable.error;
            case READY_TO_INSTALL:
                return R.drawable.install;
            case INSTALLED:
                return R.drawable.installed;
            case INSTALL_ERROR:
                return R.drawable.queued;
            case STOPPED:
                return R.drawable.stopped;
        }

        return R.drawable.queued;
    }
}