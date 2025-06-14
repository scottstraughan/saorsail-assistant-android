package com.scottstraughan.saorsailassistant.ui.activities.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.scottstraughan.saorsailassistant.notifications.NotificationManager;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.storage.PreferenceStorage;

public class SettingsFragment extends PreferenceFragmentCompat {
    /**
     * @inheritdoc
     */
    @Override
    public void onCreatePreferences(
        Bundle savedInstanceState,
        String rootKey
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    /**
     * @inheritdoc
     */
    @Override
    public void onResume() {
        super.onResume();

        Context context = this.getContext();

        if (context != null) {
            Preference pref = findPreference(getString(
                    R.string.preference_key_notifications_enabled));

            if (pref instanceof SwitchPreference) {
                SwitchPreference switchPreference = (SwitchPreference) pref;
                switchPreference.setChecked(NotificationManager.isAllowed(context));
            }

            this.setPreferenceScreen(null);
            this.addPreferencesFromResource(R.xml.preferences);
            this.reloadPreferences(context);
        }
    }

    /**
     * Reload any preferences as required.
     */
    public void reloadPreferences(
        Context context
    ) {
        Preference endpointPreference = findPreference(getString(
            R.string.preference_key_check_endpoint));

        if (endpointPreference instanceof EditTextPreference) {
            endpointPreference.setSummary(PreferenceStorage.getAppInstallCheckUrl(context));
        }
    }
}
