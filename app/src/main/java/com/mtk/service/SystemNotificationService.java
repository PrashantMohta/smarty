package com.mtk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;

import com.dandy.smartwatch.modded.R;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.Util;
import com.mtk.map.BMessage;
import java.util.HashSet;

public class SystemNotificationService extends BroadcastReceiver {
    private static final String LOG_TAG = "SystemNotificationService";
    private static float mBettryCapacity = 0.0f;
    private static float mLastBettryCapacity = 0.0f;
    private Context mContext = null;
    String packagename="";
    public SystemNotificationService() {
        Log.m2355i(LOG_TAG, "SystemNotificationService(), SystemNotificationService created!", new Object[0]);
    }

    public void onReceive(Context context, Intent intent) {
        this.mContext = context;
        String intentAction = intent.getAction();
        android.util.Log.i("System Notification",intentAction);
        if ("android.intent.action.BATTERY_LOW".equalsIgnoreCase(intentAction)) {
            if (mLastBettryCapacity == 0.0f) {
                Log.m2355i(LOG_TAG, "mLastBettryCapacity = 0", new Object[0]);
                sendLowBatteryMessage(String.valueOf(mBettryCapacity * 100.0f));
                mLastBettryCapacity = mBettryCapacity;
            } else if (mLastBettryCapacity != mBettryCapacity) {
                sendLowBatteryMessage(String.valueOf((int) (mBettryCapacity * 100.0f)));
                mLastBettryCapacity = mBettryCapacity;
            }
        } else if ("android.intent.action.BATTERY_CHANGED".equalsIgnoreCase(intentAction)) {
            mBettryCapacity = ((float) intent.getIntExtra("level", -1)) / ((float) intent.getIntExtra("scale", -1));
        } else if ("android.intent.action.ACTION_POWER_CONNECTED".equalsIgnoreCase(intentAction)) {
            mLastBettryCapacity = 0.0f;
        } else if("com.mtk.custom.PUSH_TO_WATCH".equalsIgnoreCase(intentAction)) {

            if(intent.getStringExtra("packagename")!=null)
            {
                packagename=intent.getStringExtra("packagename");
            }
            sendSystemMessage(intent.getStringExtra("title"),intent.getStringExtra("content"));

        }else if (SmsService.SMS_ACTION.equals(intentAction) && !BlockList.getInstance().getBlockList().contains(AppList.SMSRESULT_APPID)) {
            if (getResultCode() == -1) {
                sendSMSSuccessMessage();
            } else {
                sendSMSFailMessage();
            }
        }
    }

    private void sendSystemMessage(String title,String content) {
        //String titile = this.mContext.getResources().getString(R.string.sms_send);
        //String content = this.mContext.getResources().getString(R.string.sms_send_success);
        Log.m2355i(LOG_TAG, "sendSMSSuccessMessage()" + title + content, new Object[0]);
        MessageObj MessageData = new MessageObj();
        MessageData.setDataHeader(createNotificationHeader());
        MessageData.setDataBody(createNotificationBody(title, content));
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSystemNotiMessage(MessageData);
        }
    }

    private void sendLowBatteryMessage(String value) {
        Log.m2355i(LOG_TAG, "sendLowBatteryMessage()", new Object[0]);
        String titile = this.mContext.getResources().getString(R.string.batterylow);
        String content = this.mContext.getResources().getString(R.string.pleaseconnectcharger) + BMessage.SEPRATOR + value + "%";
        MessageObj smsMessageData = new MessageObj();
        smsMessageData.setDataHeader(createNotificationHeader());
        smsMessageData.setDataBody(createNotificationBody(titile, content));
        Log.m2355i(LOG_TAG, "sendSmsMessage(), smsMessageData=" + smsMessageData, new Object[0]);
        HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
        MainService service = MainService.getInstance();
        if (service != null && !blockList.contains(AppList.BETTRYLOW_APPID)) {
            service.sendSystemNotiMessage(smsMessageData);
        }
    }

    private MessageHeader createNotificationHeader() {
        MessageHeader header = new MessageHeader();
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_NOTI);
        header.setMsgId(Util.genMessageId());
        header.setAction(MessageObj.ACTION_ADD);
        Log.m2355i(LOG_TAG, "createSmsHeader(), header=" + header, new Object[0]);
        return header;
    }

    private NotificationMessageBody createNotificationBody(String title, String content) {
        ApplicationInfo appinfo = this.mContext.getApplicationInfo();

        if(!packagename.equals("")){
        appinfo =Util.getAppInfo(this.mContext,packagename);
        packagename="";
        }

        String appName = Util.getAppName(this.mContext, appinfo);
        Bitmap sendIcon = Util.getMessageIcon(this.mContext, appinfo);
        int timestamp = Util.getUtcTime(System.currentTimeMillis());
        String tickerText = "";
        NotificationMessageBody body = new NotificationMessageBody();
        if (title == this.mContext.getResources().getString(R.string.batterylow)) {
            body.setAppID(Util.getKeyFromValue(AppList.BETTRYLOW_APPID));
        } else if (title == this.mContext.getResources().getString(R.string.sms_send)) {
            body.setAppID(Util.getKeyFromValue(AppList.SMSRESULT_APPID));
        }
        body.setSender(title);
        body.setTitle(appName);
        body.setContent(content + "\n \n" +title);

        body.setTickerText(tickerText);
        body.setTimestamp(timestamp);
        body.setIcon(sendIcon);
        Log.m2355i(LOG_TAG, "createLowBatteryBody(), body=" + body.toString().substring(0, 20), new Object[0]);
        return body;
    }

    private void sendSMSSuccessMessage() {
        String titile = this.mContext.getResources().getString(R.string.sms_send);
        String content = this.mContext.getResources().getString(R.string.sms_send_success);
        Log.m2355i(LOG_TAG, "sendSMSSuccessMessage()" + titile + content, new Object[0]);
        MessageObj sendSMSSuccessMessageData = new MessageObj();
        sendSMSSuccessMessageData.setDataHeader(createNotificationHeader());
        sendSMSSuccessMessageData.setDataBody(createNotificationBody(titile, content));
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSystemNotiMessage(sendSMSSuccessMessageData);
        }
    }

    private void sendSMSFailMessage() {
        String titile = this.mContext.getResources().getString(R.string.sms_send);
        String content = this.mContext.getResources().getString(R.string.sms_send_fail);
        Log.m2355i(LOG_TAG, "sendSMSFailMessage()" + titile + content, new Object[0]);
        MessageObj sendSMSSuccessMessageData = new MessageObj();
        sendSMSSuccessMessageData.setDataHeader(createNotificationHeader());
        sendSMSSuccessMessageData.setDataBody(createNotificationBody(titile, content));
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSystemNotiMessage(sendSMSSuccessMessageData);
        }
    }
}
