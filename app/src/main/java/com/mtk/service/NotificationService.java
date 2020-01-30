package com.mtk.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.RemoteViews;

import com.mtk.BTNotificationApplication;
import com.mtk.Constants;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.IgnoreList;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.mtk.map.MapConstants;
import com.mtk.util.ToastUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class NotificationService extends AccessibilityService {
    private static final long EVENT_NOTIFICATION_TIMEOUT_MILLIS = 0;
    private static final String LOG_TAG = "NotifiService";
    private static final int NOTIFICATION_CONTENT_TYPE = 10;
    private static final int NOTIFICATION_TITLE_TYPE = 9;
    private AccessibilityEvent mAccessibilityEvent = null;
    private Notification mNotification = null;
    public static final Context sContext = BTNotificationApplication.getInstance().getApplicationContext();

    public NotificationService() {
        Log.i(LOG_TAG, "NotifiService(), NotifiService created!");
        ToastUtils.showShortToast(sContext,"Started notification service");


    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(LOG_TAG, "onAccessibilityEvent(), eventType=" + event.getEventType());
        if(PreferenceData.isNotificationPrivate())
        {if (event.getEventType() == 64) {
            this.mAccessibilityEvent = event;
            this.mNotification = (Notification) this.mAccessibilityEvent.getParcelableData();
            Log.i(LOG_TAG, "onAccessibilityEvent(),toString=" + this.mAccessibilityEvent.toString());
            if (this.mNotification != null) {
                boolean isServiceEnabled = PreferenceData.isNotificationServiceEnable();
                boolean needForward = PreferenceData.isNeedPush();
                if (isServiceEnabled && needForward) {
                    HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
                    HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
                    HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
                    if (blockList.contains(event.getPackageName()) || ignoreList.contains(event.getPackageName()) || exclusionList.contains(event.getPackageName())) {
                        Log.i(LOG_TAG, "Notice: This notification received!, package name=" + this.mAccessibilityEvent.getPackageName());
                    } else {
                        sendNotifiMessage();
                    }
                }
            }
        }
    }
    }

    private void sendNotifiMessage() {
        Log.i(LOG_TAG, "sendNotifiMessage()");
        MessageObj notificationMessage = new MessageObj();
        notificationMessage.setDataHeader(createNotificationHeader());
        notificationMessage.setDataBody(createNotificationBody());
        String msgContent = notificationMessage.getDataBody().getContent();
        String msgTitile = ((NotificationMessageBody) notificationMessage.getDataBody()).getTitle();
        String msgtickText = ((NotificationMessageBody) notificationMessage.getDataBody()).getTickerText();
        if (msgContent.length() != 0 || msgTitile.length() != 0 || msgtickText.length() != 0) {
            MainService service = MainService.getInstance();
            if (service != null) {
                service.sendNotiMessage(notificationMessage);
            }
        }
    }

    private MessageHeader createNotificationHeader() {
        MessageHeader header = new MessageHeader();
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_NOTI);
        header.setMsgId(Util.genMessageId());
        header.setAction(MessageObj.ACTION_ADD);
        Log.i(LOG_TAG, "createNotificationHeader(), header=" + header);
        return header;
    }

    private NotificationMessageBody createNotificationBody() {
        int timestamp;
        ApplicationInfo appinfo = Util.getAppInfo(getBaseContext(), this.mAccessibilityEvent.getPackageName());
        String appName = Util.getAppName(getBaseContext(), appinfo);
        Bitmap sendIcon = Util.getMessageIcon(getBaseContext(), appinfo);
        timestamp = Util.getUtcTime(System.currentTimeMillis());
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (!applist.containsValue(this.mAccessibilityEvent.getPackageName())) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max++;
            applist.put(AppList.MAX_APP, Integer.valueOf(max));
            applist.put(Integer.valueOf(max), this.mAccessibilityEvent.getPackageName());
            AppList.getInstance().saveAppList(applist);
        }
        String title = "";
        String content = "";
        String[] textList = getNotificationText();

        if (textList != null) {
            if (textList.length <= 0 || textList[0] == null) {
                if (this.mNotification.tickerText != null) {
                    title = this.mNotification.tickerText.toString();
                }
                if (title.length() > 128) {
                    title = title.substring(0, 128) + Constants.TEXT_POSTFIX;
                }
            } else {
                title = textList[0];
            }
            if (textList.length > 1 && textList[1] != null) {
                content = textList[1];
            }
            if (title.length() > 128) {
                title = title.substring(0, 128) + Constants.TEXT_POSTFIX;
            }
        }
        if (title.length() > 0) {
            title = "".concat(title).concat("");
        }
        applist = AppList.getInstance().getAppList();
        String appID = Util.getKeyFromValue(this.mAccessibilityEvent.getPackageName());
        NotificationMessageBody body = new NotificationMessageBody();
        body.setSender(appName);
        body.setAppID(appID);
        body.setTitle(title);
        body.setContent(content);
        body.setTickerText(title);
        body.setTimestamp(timestamp);
        body.setIcon(sendIcon);
        Log.i(LOG_TAG, "createNotificationBody(), body=" + body.toString().substring(0, 20));
        return body;
    }

    public void onServiceConnected() {
        Log.i(LOG_TAG, "onServiceConnected()");
        if (VERSION.SDK_INT < 14) {
            setAccessibilityServiceInfo();
        }
        MainService.setNotificationService(this);
    }

    private void setAccessibilityServiceInfo() {
        Log.i(LOG_TAG, "setAccessibilityServiceInfo()");
        AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
        accessibilityServiceInfo.eventTypes = 64;
        accessibilityServiceInfo.feedbackType = 16;
        accessibilityServiceInfo.notificationTimeout = EVENT_NOTIFICATION_TIMEOUT_MILLIS;
        setServiceInfo(accessibilityServiceInfo);
    }

    public void onInterrupt() {
        Log.i(LOG_TAG, "onInterrupt()");
    }

    public boolean onUnbind(Intent intent) {
        Log.i(LOG_TAG, "onUnbind()");
        MainService.clearNotificationService();
        return false;
    }

    @SuppressLint({"UseSparseArrays"})
    private String[] getNotificationText() {
        RemoteViews remoteViews = this.mNotification.contentView;
        if (remoteViews == null) {
            return new String[0];
        }
        Class<? extends RemoteViews> remoteViewsClass = remoteViews.getClass();
        HashMap<Integer, String> text = new HashMap();
        int notificationKey = 1;
        try {
            for (Field outerField : remoteViewsClass.getDeclaredFields()) {
                if (outerField.getName().equals("mActions")) {
                    outerField.setAccessible(true);
                    Iterator it = ((ArrayList) outerField.get(remoteViews)).iterator();
                    while (it.hasNext()) {
                        Object action = it.next();
                        Object value = null;
                        Integer type = null;
                        for (Field field : action.getClass().getDeclaredFields()) {
                            field.setAccessible(true);
                            if (field.getName().equals("value")) {
                                value = field.get(action);
                            } else if (field.getName().equals(MapConstants.TYPE)) {
                                type = Integer.valueOf(field.getInt(action));
                            }
                        }
                        if (type != null && (type.intValue() == 9 || type.intValue() == 10)) {
                            if (value != null) {
                                text.put(Integer.valueOf(notificationKey), value.toString());
                            }
                            notificationKey++;
                        }
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
        return (String[]) text.values().toArray(new String[0]);
    }
}
