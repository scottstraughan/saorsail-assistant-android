package com.scottstraughan.saorsailassistant.assistant.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.scottstraughan.saorsailassistant.assistant.scheduler.ScheduleManager;
import com.scottstraughan.saorsailassistant.logging.Logger;

/**
 * Receiver for when the network state changes.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(
        Context context,
        Intent intent
    ) {
        String action = intent.getAction();

        if (action == null) {
             return;
        }

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean noConnectivity = intent.getBooleanExtra(
                ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (!noConnectivity) {
                Logger.i("Network in connect state, scheduling sideload request checks.");
                ScheduleManager scheduleManager = new ScheduleManager();
                scheduleManager.scheduleChecks(context);
            }
        }
    }
}
