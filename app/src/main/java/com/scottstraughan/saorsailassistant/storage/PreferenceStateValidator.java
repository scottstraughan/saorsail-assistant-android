package com.scottstraughan.saorsailassistant.storage;

import android.content.Context;

/**
 * Validates the state of the preferences.
 */
public class PreferenceStateValidator {
    /**
     * Get the state of the preferences.
     */
    public static PreferenceState getState(
        Context context
    ) {
        String pairCode = PreferenceStorage.getPairCode(context);
        boolean pairCodeValid = PreferenceStorage.isPairCodeValid(context);
        boolean checkingEnabled = PreferenceStorage.isAssistantEnabled(context);

        if (pairCode == null || pairCode.isEmpty()) {
            return PreferenceState.NOT_PAIRED;
        } else if (!pairCodeValid) {
            return PreferenceState.PAIR_CODE_INVALID;
        } else if (!checkingEnabled) {
            return PreferenceState.CHECKING_DISABLED;
        }

        return PreferenceState.OK;
    }
}
