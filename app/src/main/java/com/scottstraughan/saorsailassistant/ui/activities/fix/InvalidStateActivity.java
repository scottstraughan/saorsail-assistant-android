package com.scottstraughan.saorsailassistant.ui.activities.fix;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.storage.PreferenceState;
import com.scottstraughan.saorsailassistant.storage.PreferenceStateValidator;
import com.scottstraughan.saorsailassistant.ui.activities.main.MainActivity;
import com.scottstraughan.saorsailassistant.ui.activities.pair.PairActivity;
import com.scottstraughan.saorsailassistant.ui.activities.settings.SettingsActivity;

/**
 * The main activity window.
 */
public class InvalidStateActivity extends AppCompatActivity {
    /**
     * @inheritdoc
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onResume() {
        super.onResume();

        PreferenceState preferenceState = PreferenceStateValidator.getState(this);

        TextView title = findViewById(R.id.fixTitle);
        TextView description = findViewById(R.id.fixDescription);

        Button settingsButton = findViewById(R.id.fixActionButton);
        settingsButton.setText(R.string.open_settings);
        settingsButton.setOnClickListener(v -> this.onSettingsButtonPressed());

        if (preferenceState == PreferenceState.CHECKING_DISABLED) {
            title.setText(R.string.checking_disabled_title);
            description.setText(R.string.checking_disabled_description);
        } else if (preferenceState == PreferenceState.PAIR_CODE_INVALID) {
            title.setText(R.string.pair_code_is_invalid_title);
            description.setText(R.string.pair_code_is_invalid_title_description);
        } else if (preferenceState == PreferenceState.OK) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (preferenceState == PreferenceState.NOT_PAIRED) {
            Intent intent = new Intent(this, PairActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean onCreateOptionsMenu(
        Menu menu
    ) {
        getMenuInflater().inflate(R.menu.activity_invalid_state, menu);
        return true;
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean onOptionsItemSelected(
        @NonNull MenuItem item
    ) {
        MainActivity.onSettingsButtonPressed(this);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the settings button has been pressed.
     */
    private void onSettingsButtonPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}