package com.mtk.customservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.dandy.smartwatch.modded.R;
import com.mtk.service.SystemNotificationService;

import java.util.Calendar;

/**
 * Created by Dandy on 28-02-2018.
 */

public class HourlyReceiver extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Context context=getApplicationContext();
        Calendar cal = Calendar.getInstance();
        Intent intent1 = new Intent(context, SystemNotificationService.class);
        intent1.setAction("com.mtk.custom.PUSH_TO_WATCH");
        intent1.putExtra("title","Hourly");
        intent1.putExtra("content",cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE));
        context.sendBroadcast(intent1);
        this.stopSelf();
        return null;
    }
}
