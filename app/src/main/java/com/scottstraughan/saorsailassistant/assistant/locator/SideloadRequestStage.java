package com.scottstraughan.saorsailassistant.assistant.locator;

/**
 * Represents the stage of the sideload request.
 */
public enum SideloadRequestStage {
    QUEUED,

    DOWNLOADING,
    DOWNLOAD_ERROR,

    READY_TO_INSTALL,
    INSTALL_ERROR,
    INSTALLED,

    STOPPED
}