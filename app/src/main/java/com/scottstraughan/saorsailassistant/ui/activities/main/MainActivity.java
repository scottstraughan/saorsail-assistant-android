package com.scottstraughan.saorsailassistant.ui.activities.main;

import static com.scottstraughan.saorsailassistant.assistant.install.InstallManager.createInstallIntent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.scottstraughan.saorsailassistant.assistant.AssistantState;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequestStage;
import com.scottstraughan.saorsailassistant.assistant.scheduler.ScheduleManager;
import com.scottstraughan.saorsailassistant.notifications.NotificationManager;
import com.scottstraughan.saorsailassistant.assistant.AssistantService;
import com.scottstraughan.saorsailassistant.ui.activities.fix.InvalidStateActivity;
import com.scottstraughan.saorsailassistant.ui.activities.main.listview.SideloadRequestListItem;
import com.scottstraughan.saorsailassistant.ui.activities.main.listview.SideloadRequestListViewAdapter;
import com.scottstraughan.saorsailassistant.ui.activities.pair.PairActivity;
import com.scottstraughan.saorsailassistant.storage.PreferenceState;
import com.scottstraughan.saorsailassistant.R;
import com.scottstraughan.saorsailassistant.assistant.AssistantLiveData;
import com.scottstraughan.saorsailassistant.logging.Logger;
import com.scottstraughan.saorsailassistant.ui.activities.settings.SettingsActivity;

/**
 * The main activity window.
 */
public class MainActivity extends AppCompatActivity {
    private final Observer<AssistantState> checkerStateObserver = this::setCheckingState;
    private ImageButton syncImageButton;
    private SideloadRequestListViewAdapter sideloadRequestListViewAdapter;
    private MainActivityViewModel viewModel;

    /**
     * @inheritdoc
     */
    @Override
    protected void onCreate(
        Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        this.viewModel = new ViewModelProvider(this)
            .get(MainActivityViewModel.class);

        AssistantLiveData checkerLiveData = AssistantLiveData.getInstance();
        checkerLiveData.observe(this, this.viewModel::reflectAssistantState);

        this.viewModel.getUiState().observe(this, uiState -> {
            this.setPreferenceState(uiState.preferenceState);
            this.sideloadRequestListViewAdapter.setItems(uiState.sideloadRequests);
            this.setSomethingHappening(
                uiState.isAssistantServiceRunning && !uiState.sideloadRequests.isEmpty());
        });

        this.viewModel.validate(this);

        this.sideloadRequestListViewAdapter = new SideloadRequestListViewAdapter(this);

        ListView downloadTaskListView = findViewById(R.id.downloadTaskListView);
        downloadTaskListView.setAdapter(this.sideloadRequestListViewAdapter);

        downloadTaskListView.setOnItemClickListener((parent, view, position, id) ->
            this.onSideloadRequestClicked(
                this.sideloadRequestListViewAdapter.getItem(position)));

        TextView fixTitle = findViewById(R.id.fixTitle);
        fixTitle.setText(R.string.nothing_to_install);

        TextView fixDescription = findViewById(R.id.fixDescription);
        fixDescription.setText(R.string.nothing_to_install_description);

        Button openWebsiteButton = findViewById(R.id.fixActionButton);
        openWebsiteButton.setText(R.string.open_saorsail_com);
        openWebsiteButton.setOnClickListener(i -> {
            Intent browserIntent = new Intent(
                Intent.ACTION_VIEW, Uri.parse("https://www.saorsail.com"));
            startActivity(browserIntent);
        });

        NotificationManager.registerNotificationChannels(this);

        this.startServices();
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onResume() {
        super.onResume();

        MainActivityViewModel
            .getAssistantLiveData()
            .observe(this, this.checkerStateObserver);

        this.viewModel.validate(this);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void onPause() {
        super.onPause();

        AssistantLiveData
            .getInstance()
            .removeObserver(this.checkerStateObserver);
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
     * @inheritdoc
     */
    @Override
    public boolean onCreateOptionsMenu(
        Menu menu
    ) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        final MenuItem refreshItem = menu.findItem(R.id.activity_main_sync_button);
        refreshItem.setActionView(R.layout.toolbar_sync_image_button);

        this.syncImageButton = (ImageButton) refreshItem.getActionView();

        if (this.syncImageButton != null) {
            this.syncImageButton.setOnClickListener(i ->
                this.onHandleToolbarItemClicked( R.id.activity_main_sync_button ));
        }

        return true;
    }

    /**
     * @inheritdoc
     */
    @Override
    public boolean onOptionsItemSelected(
        MenuItem item
    ) {
        this.onHandleToolbarItemClicked(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set if something is happening in the UI.
     * @param checking true or not.
     */
    private void setSomethingHappening(
        boolean checking
    ) {
        View somethingHappeningView = findViewById(R.id.something_happening);
        View nothingHappeningView = findViewById(R.id.nothing_happening);

        if (checking || AssistantLiveData.getInstance().hasItems()) {
            somethingHappeningView.setVisibility(View.VISIBLE);
            nothingHappeningView.setVisibility(View.GONE);
        } else {
            somethingHappeningView.setVisibility(View.GONE);
            nothingHappeningView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when an item in the sideload request list is clicked.
     * @param sideloadRequestListItem the clicked item
     */
    private void onSideloadRequestClicked(
        SideloadRequestListItem sideloadRequestListItem
    ) {
        if (sideloadRequestListItem.getSideloadRequest().getStage()
            == SideloadRequestStage.READY_TO_INSTALL) {
            Intent installIntent = createInstallIntent(
                this, sideloadRequestListItem.getSideloadRequest());

            startActivity(installIntent);
        } else if (
            sideloadRequestListItem.getSideloadRequest().getStage()
                == SideloadRequestStage.STOPPED
                || sideloadRequestListItem.getSideloadRequest().getStage()
                == SideloadRequestStage.DOWNLOAD_ERROR) {
            SideloadRequest sideloadRequest =
                sideloadRequestListItem.getSideloadRequest();

            sideloadRequest.setStage(
                SideloadRequestStage.QUEUED);

            AssistantLiveData
                .getInstance()
                .updateSideloadRequest(sideloadRequest);

            this.startAssistantService(false);
        }
    }

    private void setPreferenceState(
        PreferenceState preferenceState
    ) {
        if (preferenceState != PreferenceState.OK) {
            Intent targetIntent = new Intent(this, InvalidStateActivity.class);

            if (preferenceState == PreferenceState.NOT_PAIRED) {
                targetIntent = new Intent(this, PairActivity.class);
            }

            Logger.i("STARTING PAIR ACTIVITY");
            startActivity(targetIntent);
            finish();
        }

        //this.prepare();
    }

    /**
     * Handle toolbar items (custom or native) being clicked.
     * @param toolbarItemId the id of the item in the view
     */
    private void onHandleToolbarItemClicked(
        int toolbarItemId
    ) {
        if (toolbarItemId == R.id.activity_main_settings_button) {
            onSettingsButtonPressed(this);
        } else if (toolbarItemId == R.id.activity_main_sync_button) {
            if (AssistantLiveData.getInstance().getValue().isChecking()) {
                this.forceStopAssistantService();
            } else {
                this.startAssistantService(true);
            }
        }
    }

    /**
     * Start any of the services if required.
     */
    private void startServices() {
        Logger.i("startServices() called, setting things up..");

        ScheduleManager scheduleManager = new ScheduleManager();
        scheduleManager.scheduleChecks(this);
    }

    /**
     * Check now for installs.
     */
    private void startAssistantService(
        boolean restartStopped
    ) {
        Intent checkIntent = new Intent(this, AssistantService.class);

        if (restartStopped) {
            checkIntent.putExtra("resetStopped", true);
        }

        startService(checkIntent);
    }

    /**
     * Force stop the assistant service/worker
     */
    private void forceStopAssistantService() {
        stopService(new Intent(this, AssistantService.class));
        ScheduleManager.stopAssistant();
    }

    /**
     * Set the global checking state.
     * @param checkingState state to use.
     */
    private void setCheckingState(
        AssistantState checkingState
    ) {
        this.enableLoadingAnimation(checkingState.isChecking());
    }

    /**
     * Start of stop the loading state indicator.
     * @param start true to start, false otherwise
     */
    private void enableLoadingAnimation(
        boolean start
    ) {
        if (this.syncImageButton == null) {
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(
            0f, -360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setDuration(1000);


        if (start && this.syncImageButton.getAnimation() == null) {
            this.syncImageButton.startAnimation(rotateAnimation);
        } else if (!start) {
            this.syncImageButton.clearAnimation();
        }
    }

    /**
     * Called when the settings button has been pressed.
     */
    public static void onSettingsButtonPressed(
        Context context
    ) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }
}