package com.scottstraughan.saorsailassistant.ui.activities.pair;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.storage.PreferenceState;
import com.scottstraughan.saorsailassistant.storage.PreferenceStateValidator;
import com.scottstraughan.saorsailassistant.ui.activities.main.MainActivity;
import com.scottstraughan.saorsailassistant.ui.activities.qr.QRCodeScanView;
import com.scottstraughan.saorsailassistant.ui.activities.settings.SettingsActivity;

/**
 * The main activity window.
 */
public class PairActivity extends AppCompatActivity {
    /**
     * @inheritdoc
     */
    @Override
    protected void onCreate(
        Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pair);

        Button pairQrButton = findViewById(R.id.pairViaQrButton);
        pairQrButton.setOnClickListener(i -> onScanQrButtonPressed());

        Button pairManuallyButton = findViewById(R.id.pairManuallyButton);
        pairManuallyButton.setOnClickListener(i -> onPairManuallyButtonPressed());
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onResume() {
        super.onResume();

        // This activity should only show when we are in a NOT_PAIRED state
        if (PreferenceStateValidator.getState(this) != PreferenceState.NOT_PAIRED) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Called when the QR code view completes.
     */
    @Override
    protected void onActivityResult(
        int requestCode,
        int resultCode,
        Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        String qrCode = data.getStringExtra("qr");

        Logger.i("Received QR:" + qrCode);
    }

    /**
     * Called when the scan QR button has been clicked. Starts the QR code scanning API.
     */
    private void onScanQrButtonPressed() {
        Intent intent = new Intent(this, QRCodeScanView.class);
        startActivity(intent);
    }

    /**
     * Called when the manual pair button has been pressed.
     */
    private void onPairManuallyButtonPressed() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}