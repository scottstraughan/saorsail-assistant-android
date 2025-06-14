package com.scottstraughan.saorsailassistant.ui.activities.main.listview;

import androidx.annotation.Nullable;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;

/**
 * Represents a sideload request within the list view.
 */
public class SideloadRequestListItem {
    private final String title;
    private final SideloadRequest sideloadRequest;

    /**
     * Constructor,
     */
    public SideloadRequestListItem(
        SideloadRequest sideloadRequest
    ) {
        this.sideloadRequest = sideloadRequest;
        this.title = sideloadRequest.getFriendlyName();
    }

    /**
     * Get the id for the view.
     * @return id
     */
    public long getId() {
        return this.sideloadRequest.getId();
    }

    /**
     * Get the title for the view.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the status message.
     */
    public String getStatusMessage() {
        return this.sideloadRequest.getStatusMessage();
    }

    /**
     * Get the sideload request
     */
    public SideloadRequest getSideloadRequest() {
        return this.sideloadRequest;
    }

    /**
     * Get the icon to show in the view.
     * @return the icon url
     */
    public String getIcon() {
        return sideloadRequest.getIconUrl();
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SideloadRequestListItem)) {
            return false;
        }

        SideloadRequestListItem compare = (SideloadRequestListItem) obj;
        return this.getId() == compare.getId();
    }
}
