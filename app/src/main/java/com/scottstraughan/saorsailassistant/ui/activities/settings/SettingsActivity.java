package com.scottstraughan.saorsailassistant.ui.activities.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.scottstraughan.saorsailassistant.notifications.NotificationManager;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.storage.PreferenceStorage;

import java.util.Objects;

public class SettingsActivity
    extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SettingsFragment fragment;

    /**
     * @inheritdoc
     */
    @Override
    protected void onCreate(
        Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        this.setTitle("Settings");

        this.fragment = new SettingsFragment();

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainerView, this.fragment)
            .commit();
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onResume() {
        super.onResume();

        PreferenceStorage.setNotificationsEnabled(
                this, NotificationManager.isAllowed(this));

        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean onOptionsItemSelected(
        MenuItem item
    ) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * @inheritdoc
     */
    @Override
    public void onSharedPreferenceChanged(
        SharedPreferences sharedPreferences,
        @Nullable String key
    ) {
        if (Objects.equals(key, getString(R.string.preference_key_notifications_enabled))) {
            NotificationManager.askPermission(this);
        }

        this.fragment.reloadPreferences(this);
    }
}