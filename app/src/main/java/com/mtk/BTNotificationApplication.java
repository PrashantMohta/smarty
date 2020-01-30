package com.mtk;

import android.app.Activity;
import android.app.Application;
import com.mtk.data.Log;
import com.mtk.data.MyExceptionHandel;
import java.util.LinkedList;
import java.util.List;

public class BTNotificationApplication extends Application {
    private static final String LOG_TAG = "BTNoticationApplication";
    private static BTNotificationApplication sInstance = null;
    private final List<Activity> activityList = new LinkedList();

    public static BTNotificationApplication getInstance() {
        return sInstance;
    }

    public void onCreate() {
        super.onCreate();

        Log.m2355i(LOG_TAG, "onCreate(), BTNoticationApplication create!", new Object[0]);
        MyExceptionHandel.getInstance().init(getApplicationContext());
        sInstance = this;
    }

    public void exit() {
        for (Activity activity : this.activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
