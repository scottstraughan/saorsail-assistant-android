package com.scottstraughan.saorsailassistant.ui.activities.qr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.logging.Logger;

/**
 * Represents the QR code scanning view.
 */
public class QRCodeScanView extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    /**
     * @inheritdoc
     */
    @Override
    protected void onCreate(
        Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        CodeScannerView scannerView = findViewById(R.id.qrCodeScannerView);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            Toast.makeText(QRCodeScanView.this, result.getText(), Toast.LENGTH_SHORT).show();
            this.success(result.getText());
        }));

        mCodeScanner.setErrorCallback(result -> runOnUiThread(this::error));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    /**
     * Called when there was a successful QR code scanner.
     * @param qrCode the code
     */
    private void success(
        String qrCode
    ) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("qr", qrCode);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    /**
     * Called when there is an error scanning the QR code.
     */
    private void error() {
        Logger.e("Error loading QR code.");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }
}