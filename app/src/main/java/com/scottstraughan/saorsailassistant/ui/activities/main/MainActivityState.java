package com.scottstraughan.saorsailassistant.ui.activities.main;

import com.scottstraughan.saorsailassistant.assistant.locator.SideloadRequest;
import com.scottstraughan.saorsailassistant.storage.PreferenceState;

import java.util.ArrayList;

/**
 * View state for the MainActivity.
 */
public class MainActivityState {
   /**
    * Any sideload requests we want to show in the UI.
    */
   public final ArrayList<SideloadRequest> sideloadRequests = new ArrayList<>();

   /**
    * If the assistant is working or not.
    */
   public boolean isAssistantServiceRunning = false;

   /**
    * The state of the preferences.
    */
   public PreferenceState preferenceState;
}
