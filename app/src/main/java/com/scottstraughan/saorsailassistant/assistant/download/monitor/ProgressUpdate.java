package com.scottstraughan.saorsailassistant.assistant.download.monitor;

public class ProgressUpdate {
    private final int progressPercentage;

    public ProgressUpdate(
        int progressPercentage
    ) {
        this.progressPercentage = progressPercentage;
    }

    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    public boolean isComplete() {
        return this.progressPercentage == 100;
    }

    public boolean isStarted() {
        return this.progressPercentage > 0;
    }
}