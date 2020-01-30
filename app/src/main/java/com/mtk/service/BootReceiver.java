package com.mtk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mtk.data.Log;
import com.mtk.data.PreferenceData;

public class BootReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = "BootReceiver";

    public BootReceiver() {
        Log.m2355i(LOG_TAG, "BootReceiver(), BootReceiver created!", new Object[0]);
    }

    public void onReceive(Context context, Intent intent) {
        boolean isServiceEnabled;
        Log.m2355i(LOG_TAG, "onReceive(), action=" + intent.getAction(), new Object[0]);
        if (PreferenceData.isNotificationServiceEnable() || PreferenceData.isSmsServiceEnable()) {
            isServiceEnabled = true;
        } else {
            isServiceEnabled = false;
        }
        Log.m2355i(LOG_TAG, "BootReceiver(), isServiceEnabled=" + isServiceEnabled, new Object[0]);
        if (isServiceEnabled) {
            Log.m2355i(LOG_TAG, "BootReceiver(), Start MainService!", new Object[0]);
            context.startService(new Intent(context, MainService.class));
        }
    }
}
