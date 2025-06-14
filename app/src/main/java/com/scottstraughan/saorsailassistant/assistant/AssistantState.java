package com.scottstraughan.saorsailassistant.assistant;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * Represents the internal state of the checker service.
 */
public final class AssistantState {
    /**
     * A map of all the sideload requests.
     */
    private final HashMap<Long, SideloadRequest> sideloadRequests
        = new HashMap<>();

    /**
     * Global running state
     */
    private boolean running = false;

    /**
     * Check if any of the sideload requests are active.
     */
    public boolean isChecking() {
        if (this.running) {
            return true;
        }

        for (SideloadRequest sideloadRequest
            : this.sideloadRequests.values()) {
            if (sideloadRequest.isDoingSomething()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a sideload request.
     */
    public void addSideloadRequest(
        SideloadRequest sideloadRequest
    ) {
        this.sideloadRequests.put(
            sideloadRequest.getId(), sideloadRequest);
    }

    /**
     * Check if we have an existing request by download url.
     */
    public boolean hasSideloadRequests(
        SideloadRequest sideloadRequest
    ) {
        for (SideloadRequest current : this.sideloadRequests.values()) {
            if (Objects.equals(
                current.getAppPackageDownloadUrl(),
                sideloadRequest.getAppPackageDownloadUrl())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return collection of install requests.
     */
    public Collection<SideloadRequest> getSideloadRequests() {
        return this.sideloadRequests.values();
    }

    /**
     * Set the global running state.
     */
    public void setRunning(
        boolean running
    ) {
        this.running = running;
    }

    /**
     * Get the global running state.
     */
    public boolean isRunning() {
        return this.running;
    }
}
