package com.mtk.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.RemoteViews;
import com.mtk.data.MessageObj;
import com.dandy.smartwatch.modded.R;
import com.mtk.service.SystemNotificationService;

public class NotifiUtils {
    public static int notifictionId = 400;

    private NotifiUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void sendNotificationToWatch(final Context context, final String title, final String content, final int delay)
    {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    Intent intent1 = new Intent(context, SystemNotificationService.class);
                    intent1.setAction("com.mtk.custom.PUSH_TO_WATCH");
                    intent1.putExtra("title",title);
                    intent1.putExtra("content",content);
                    context.sendBroadcast(intent1);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        t.start();

    }

    public static void sendAppNotificationToWatch(final Context context, final String title, final String content, final String packagename)
    {
        Intent intent1 = new Intent(context, SystemNotificationService.class);
        intent1.setAction("com.mtk.custom.PUSH_TO_WATCH");
        intent1.putExtra("title",title);
        intent1.putExtra("content",content);
        intent1.putExtra("packagename",packagename);
        context.sendBroadcast(intent1);
    }

    @SuppressLint({"NewApi", "WrongConstant"})
    public static void showNotification(Context context, Bitmap bitmap, String easetitle, String title, Intent intent, String content) {
        @SuppressLint("WrongConstant") NotificationManager nm = (NotificationManager) context.getSystemService(MessageObj.CATEGORY_NOTI);
        Notification notification = new Builder(context).build();
        notification.icon = R.drawable.ic_launcher;
        notification.tickerText = easetitle;
        notification.when = System.currentTimeMillis();
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.custom_notification);
        rv.setImageViewBitmap(R.id.iv_notifi_icon, bitmap);
        rv.setTextViewText(R.id.tv_notifi_title, title);
        rv.setTextViewText(R.id.tv_notifi_content, content);
        notification.ledARGB = 576;
        notification.contentView = rv;
        notification.contentIntent = PendingIntent.getActivity(context, 100, intent, 1073741824);
        notification.flags = 16;
        notification.defaults = -1;
        int i = notifictionId;
        notifictionId = i + 1;
        nm.notify(i, notification);
    }
}
