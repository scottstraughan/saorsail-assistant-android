package com.scottstraughan.saorsailassistant.ui.activities.main;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.scottstraughan.saorsailassistant.assistant.AssistantLiveData;
import com.scottstraughan.saorsailassistant.assistant.AssistantState;
import com.scottstraughan.saorsailassistant.storage.PreferenceStateValidator;

public class MainActivityViewModel extends ViewModel {
    private final MutableLiveData<MainActivityState> uiState =
        new MutableLiveData<>(new MainActivityState());

    /**
     * Get the UI state.
     */
    public LiveData<MainActivityState> getUiState() {
        return uiState;
    }

    /**
     * Get the assistant live date.
     */
    public static AssistantLiveData getAssistantLiveData() {
        return AssistantLiveData.getInstance();
    }

    /**
     * Update the view model state based on the assistant state.
     */
    public void reflectAssistantState(
        AssistantState assistantState
    ) {
        MainActivityState mainActivityModel = this.uiState.getValue();

        if (mainActivityModel ==  null) {
            return;
        }

        mainActivityModel.isAssistantServiceRunning = assistantState.isChecking();
        mainActivityModel.sideloadRequests.addAll(assistantState.getSideloadRequests());

        this.uiState.postValue(mainActivityModel);
    }

    /**
     * Validate all state.
     */
    public void validate(
        Context context
    ) {
        MainActivityState mainActivityModel = this.uiState.getValue();

        if (mainActivityModel == null) {
            return;
        }

        mainActivityModel.preferenceState = PreferenceStateValidator.getState(context);
        this.uiState.postValue(mainActivityModel);
    }
}