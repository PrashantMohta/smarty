package com.mtk.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.telephony.TelephonyManager;
import com.mtk.BTNotificationApplication;
import com.mtk.UI.Smartwatch;
import com.mtk.btconnection.BluetoothManager;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.CallMessageBody;
import com.mtk.data.Log;
import com.mtk.data.LogUtil;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.NoDataException;
import com.mtk.data.NotificationMessageBody;
import com.mtk.data.PreferenceData;
import com.mtk.data.SmsMessageBody;
import com.mtk.data.Util;
import com.mtk.map.BTMapService;
import com.mtk.map.MapConstants;
import com.mtk.map.SmsController;
import com.mtk.remotecamera.RemoteCameraService;
import com.dandy.smartwatch.modded.R;
import com.mtk.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;

public final class MainService extends Service {
    private static final String LOG_TAG = "MainService";
    public static final Context sContext = BTNotificationApplication.getInstance().getApplicationContext();
    public static BluetoothManager mBluetoothManager = new BluetoothManager(sContext);
    public static final boolean mIsNeedStartBTMapService = true;
    private static MainService sInstance = null;
    private static NotificationService sNotificationService = null;
    private final BroadcastReceiver mBTManagerReceiver = new C05722();
    private BTMapService mBTMapService = null;
    private final ContentObserver mCallLogObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            if (MainService.this.getMissedCallCount() == 0) {
                MainService.this.sendReadMissedCallData();
            }
        }
    };
    private CallService mCallService = null;
    private boolean mIsConnectionStatusIconShow = false;
    private boolean mIsMainServiceActive = false;
    private RemoteCameraService mRemoteCameraService = null;
    private SmsService mSmsService = null;
    private SystemNotificationService mSystemNotificationService = null;

    class C05722 extends BroadcastReceiver {
        C05722() {
        }

        public void onReceive(Context context, Intent intent) {
            if (BluetoothManager.BT_BROADCAST_ACTION.equals(intent.getAction())) {
                int extraType = intent.getIntExtra(BluetoothManager.EXTRA_TYPE, 0);
                byte[] mIncomingMessageBuffer = intent.getByteArrayExtra("EXTRA_DATA");
                Log.m2355i(MainService.LOG_TAG, "onReceive(), extraType=" + extraType, new Object[0]);
                switch (extraType) {
                    case 1:
                        MainService.this.updateConnectionStatus(false);
                        return;
                    case 2:
                        Intent broadcastIntent = new Intent();
                        broadcastIntent.setAction(MapConstants.BT_MAP_BROADCAST_ACTION);
                        broadcastIntent.putExtra(MapConstants.DISCONNECT, MapConstants.DISCONNECT);
                        MainService.sContext.sendBroadcast(broadcastIntent);
                        Intent broadcastExitIntent = new Intent();
                        broadcastExitIntent.setAction(RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION);
                        context.sendBroadcast(broadcastExitIntent);
                        MainService.this.updateConnectionStatus(false);
                        return;
                    case 4:
                        try {
                            MainService.this.parseReadBuffer(mIncomingMessageBuffer);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    case 5:
                        Log.m2355i(MainService.LOG_TAG, "MAP REQUEST ARRIVE", new Object[0]);
                        try {
                            if (Integer.valueOf(new String(intent.getByteArrayExtra("EXTRA_DATA"), MessageObj.CHARSET).split(" ")[0]).intValue() == 8) {
                                MainService.this.sendMapResult(String.valueOf(1));
                                if (MainService.this.mBTMapService == null) {
                                    MainService.this.startMapService();
                                    return;
                                }
                                return;
                            }
                            return;
                        } catch (UnsupportedEncodingException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    default:
                        return;
                }
            }
        }
    }

    public MainService() {
        Log.m2355i(LOG_TAG, "MainService(), MainService in construction!", new Object[0]);
    }

    public static MainService getInstance() {
        if (sInstance == null) {
            Log.m2355i(LOG_TAG, "getInstance(), Main service is null.", new Object[0]);
            startMainService();
        }
        return sInstance;
    }

    private static void startMainService() {
        Log.m2355i(LOG_TAG, "startMainService()", new Object[0]);
        sContext.startService(new Intent(sContext, MainService.class));
    }

    private static void stopMainService() {
        Log.m2355i(LOG_TAG, "stopMainService()", new Object[0]);
    }

    public static void setNotificationService(NotificationService notificationService) {
        sNotificationService = notificationService;
    }

    public static void clearNotificationService() {
        sNotificationService = null;
    }

    public static boolean isNotificationServiceActived() {
        return sNotificationService != null;
    }

    public void onCreate() {
        Log.m2355i(LOG_TAG, "onCreate()", new Object[0]);
        updateConnectionStatus(false);
        super.onCreate();
        sInstance = this;
        this.mIsMainServiceActive = true;
        Map<Object, Object> applist = AppList.getInstance().getAppList();
        if (applist.size() == 0) {
            applist.put(AppList.MAX_APP, Integer.valueOf(3));
            applist.put(Integer.valueOf(3), AppList.BETTRYLOW_APPID);
            applist.put(Integer.valueOf(3), AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.BETTRYLOW_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max++;
            applist.put(AppList.MAX_APP, Integer.valueOf(max));
            applist.put(Integer.valueOf(max), AppList.BETTRYLOW_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        if (!applist.containsValue(AppList.SMSRESULT_APPID)) {
            int max = Integer.parseInt(applist.get(AppList.MAX_APP).toString());
            applist.remove(AppList.MAX_APP);
            max++;
            applist.put(AppList.MAX_APP, Integer.valueOf(max));
            applist.put(Integer.valueOf(max), AppList.SMSRESULT_APPID);
            AppList.getInstance().saveAppList(applist);
        }
        initBluetoothManager();
        registerService();
    }

    public void onDestroy() {
        Log.m2355i(LOG_TAG, "onDestroy()", new Object[0]);
        this.mIsMainServiceActive = false;
        unregisterReceiver(this.mSystemNotificationService);
        this.mSystemNotificationService = null;
        getContentResolver().unregisterContentObserver(this.mCallLogObserver);
        stopRemoteCameraService();
        stopMapService();
        stopSmsService();
        destoryBluetoothManager();
        LogUtil.getInstance(sContext).stop();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isMainServiceActive() {
        return this.mIsMainServiceActive;
    }

    private void initBluetoothManager() {
        mBluetoothManager.setupConnection();
        sContext.registerReceiver(this.mBTManagerReceiver, new IntentFilter(BluetoothManager.BT_BROADCAST_ACTION));
    }

    private void destoryBluetoothManager() {
        mBluetoothManager.saveData();
        mBluetoothManager.removeConnection();
        sContext.unregisterReceiver(this.mBTManagerReceiver);
    }

    public void sendNotiMessage(MessageObj notiMessage) {
        Log.m2355i(LOG_TAG, "sendNotiMessage(),  notiMessageId=" + notiMessage.toString(), new Object[0]);
        sendData(notiMessage);
    }

    public void sendSystemNotiMessage(MessageObj notiMessage) {
        Log.m2355i(LOG_TAG, "sendOtherNotiMessage(),  OtherNotiMessageID=" + notiMessage.getDataHeader().getMsgId(), new Object[0]);
        sendData(notiMessage);
    }

    public void sendSmsMessage(MessageObj smsMessage) {
        Log.m2355i(LOG_TAG, "sendSmsMessage(),  smsMessageId=" + smsMessage.getDataHeader().getMsgId(), new Object[0]);
        sendData(smsMessage);
    }

    public void sendCallMessage(MessageObj callMessage) {
        Log.m2355i(LOG_TAG, "sendSmsMessage(),  smsMessageId=" + callMessage.getDataHeader().getMsgId(), new Object[0]);
        sendData(callMessage);
    }

    public void sendMapResult(String result) {
        mBluetoothManager.sendMapResult(result);
    }

    public void sendMapDResult(String result) {
        mBluetoothManager.sendMapDResult(result);
    }

    public void sendMapData(byte[] data) {
        mBluetoothManager.sendMAPData(data);
    }

    public void sendCAPCResult(String result) {
        mBluetoothManager.sendCAPCResult(result);
    }

    public void sendCAPCData(byte[] data) {
        mBluetoothManager.sendCAPCData(data);
    }

    public void sendMREEResult(String result) {
        mBluetoothManager.sendMREEResult(result);
    }

    public void sendMREEData(byte[] data) {
        mBluetoothManager.sendMREEData(data);
    }

    private void sendData(MessageObj dataObj) {
        byte[] data = genBytesFromObject(dataObj);
        if (data == null) {
            Log.m2355i(LOG_TAG, "sendData(),  genBytesFromObject failed!", new Object[0]);
            return;
        }
        mBluetoothManager.sendData(data);
        Log.m2355i(LOG_TAG, "sendData(), data=" + Arrays.toString(data), new Object[0]);
    }

    private byte[] genBytesFromObject(MessageObj dataObj) {
        Log.m2355i(LOG_TAG, "genBytesFromObject(), dataObj=" + dataObj, new Object[0]);
        if (dataObj == null) {
            return null;
        }
        byte[] bArr = null;
        try {
            return dataObj.genXmlBuff();
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
            return bArr;
        } catch (IllegalStateException e12) {
            e12.printStackTrace();
            return bArr;
        } catch (IOException e13) {
            e13.printStackTrace();
            return bArr;
        } catch (XmlPullParserException e14) {
            e14.printStackTrace();
            return bArr;
        } catch (NoDataException e) {
            e.printStackTrace();
            return bArr;
        }
    }

    public void receiveData(byte[] data) {
        Log.m2355i(LOG_TAG, "sendData(), data.length=" + data.length, new Object[0]);
        if (createObjectFromBytes(data) != null) {
        }
    }

    private MessageObj createObjectFromBytes(byte[] data) {
        if (data == null) {
            return null;
        }
        MessageObj dataObj = null;
        try {
            dataObj = new MessageObj().parseXml(data);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Log.m2355i(LOG_TAG, "genObjectFromBytes(), dataObj=" + dataObj, new Object[0]);
        return dataObj;
    }

    public void updateConnectionStatus(boolean isCrash) {
        boolean isShowNotification;
        if (PreferenceData.isShowConnectionStatus() && mBluetoothManager.isBTConnected()) {
            isShowNotification = true;
        } else {
            isShowNotification = false;
        }
        Log.m2355i(LOG_TAG, "showConnectionStatus(), showNotification=" + isShowNotification, new Object[0]);
        @SuppressLint("WrongConstant") NotificationManager manager = (NotificationManager) sContext.getSystemService(MessageObj.CATEGORY_NOTI);
        if (isCrash) {
            manager.cancel(R.string.app_name);
            this.mIsConnectionStatusIconShow = false;
        } else if (isShowNotification) {
            Notification notification = new Notification();
            notification.icon = R.drawable.ic_connected_status;
            notification.tickerText = sContext.getText(R.string.notification_ticker_text);
            notification.flags = 2;
            notification.setLatestEventInfo(sContext, sContext.getText(R.string.notification_title), sContext.getText(R.string.notification_content), PendingIntent.getActivity(sContext, 0, new Intent(sContext, Smartwatch.class), 67108864));
            Log.m2355i(LOG_TAG, "updateConnectionStatus(), show notification=" + notification, new Object[0]);
            manager.notify(R.string.app_name, notification);
            this.mIsConnectionStatusIconShow = true;
        } else if (this.mIsConnectionStatusIconShow) {
            manager.cancel(R.string.app_name);
            this.mIsConnectionStatusIconShow = false;
            Log.m2355i(LOG_TAG, "updateConnectionStatus(),  cancel notification id=2131165211", new Object[0]);
        }
    }

    public boolean sendDataTest(byte[] data) {
        return mBluetoothManager.sendData(data);
    }

    private void registerService() {
        Log.m2355i(LOG_TAG, "registerService()", new Object[0]);
        startSystemNotificationService();
        startRemoteCameraService();
        startMapService();
        if (PreferenceData.isSmsServiceEnable()) {
            startSmsService();
        }
        if (PreferenceData.isCallServiceEnable()) {
            getContentResolver().registerContentObserver(Calls.CONTENT_URI, true, this.mCallLogObserver);
            startCallService();
        }
    }

    private boolean isAllServiceDisable() {
        boolean allServiceDisable;
        if (PreferenceData.isNotificationServiceEnable() || PreferenceData.isSmsServiceEnable() || PreferenceData.isCallServiceEnable()) {
            allServiceDisable = false;
        } else {
            allServiceDisable = true;
        }
        Log.m2355i(LOG_TAG, "isAllServiceDisable(), allServiceDisable=" + allServiceDisable, new Object[0]);
        return allServiceDisable;
    }

    public void startNotificationService() {
        Log.m2355i(LOG_TAG, "startNotifiService()", new Object[0]);
        if (!this.mIsMainServiceActive) {
            startMainService();
        }
    }

    public void stopNotificationService() {
        Log.m2355i(LOG_TAG, "stopNotifiService()", new Object[0]);
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    void startSystemNotificationService() {
        this.mSystemNotificationService = new SystemNotificationService();
        registerReceiver(this.mSystemNotificationService, new IntentFilter("android.intent.action.BATTERY_LOW"));
        registerReceiver(this.mSystemNotificationService, new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED"));
        registerReceiver(this.mSystemNotificationService, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        String action = SmsService.SMS_ACTION;
        IntentFilter filtersms = new IntentFilter();
        filtersms.addAction(action);
        registerReceiver(this.mSystemNotificationService, filtersms);
    }

    public void startSmsService() {
        Log.m2355i(LOG_TAG, "startSmsService()", new Object[0]);
        if (!this.mIsMainServiceActive) {
            startMainService();
        }
        this.mSmsService = new SmsService();
        registerReceiver(this.mSmsService, new IntentFilter("com.mtk.btnotification.SMS_RECEIVED"));
    }

    public void stopSmsService() {
        Log.m2355i(LOG_TAG, "stopSmsService()", new Object[0]);
        if (this.mSmsService != null) {
            unregisterReceiver(this.mSmsService);
            this.mSmsService = null;
        }
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    public void startCallService() {
        Log.m2355i(LOG_TAG, "startCallService()", new Object[0]);
        if (!this.mIsMainServiceActive) {
            startMainService();
        }
        this.mCallService = new CallService(sContext);
        ((TelephonyManager) sContext.getSystemService("phone")).listen(this.mCallService, 32);
    }

    @SuppressLint("WrongConstant")
    public void stopCallService() {
        Log.m2355i(LOG_TAG, "stopCallService()", new Object[0]);
        if (this.mCallService != null) {
            ((TelephonyManager) sContext.getSystemService("phone")).listen(this.mCallService, 0);
            this.mCallService = null;
        }
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    void startMapService() {
        Log.m2355i(LOG_TAG, "startMapService()", new Object[0]);
        if (!this.mIsMainServiceActive) {
            startMainService();
        }
        if (this.mBTMapService == null) {
            this.mBTMapService = new BTMapService();
            registerReceiver(this.mBTMapService, new IntentFilter(MapConstants.BT_MAP_BROADCAST_ACTION));
        }
    }

    void startRemoteCameraService() {
        Log.m2355i(LOG_TAG, "startRemoteCameraService()", new Object[0]);
        if (!this.mIsMainServiceActive) {
            startMainService();
        }
        if (this.mRemoteCameraService == null) {
            this.mRemoteCameraService = new RemoteCameraService();
            registerReceiver(this.mRemoteCameraService, new IntentFilter(RemoteCameraService.BT_REMOTECAMERA_BROADCAST_ACTION));
        }
    }

    void stopMapService() {
        Log.m2355i(LOG_TAG, "stopMapService()", new Object[0]);
        if (this.mBTMapService != null) {
            new SmsController(sContext).clearDeletedMessage();
            unregisterReceiver(this.mBTMapService);
            this.mBTMapService = null;
        }
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    void stopRemoteCameraService() {
        Log.m2355i(LOG_TAG, "stopRemoteCameraService()", new Object[0]);
        if (this.mRemoteCameraService != null) {
            unregisterReceiver(this.mRemoteCameraService);
            this.mRemoteCameraService = null;
        }
        if (isAllServiceDisable()) {
            stopMainService();
        }
    }

    public void startRingService() {
        Log.m2355i(LOG_TAG, "startRingService()", new Object[0]);
        sContext.startService(new Intent(sContext, RingReciver.class));
    }

    public void stopRingService() {
        Log.m2355i(LOG_TAG, "stopRingService()", new Object[0]);
        sContext.stopService(new Intent(sContext, RingReciver.class));
    }

    private void parseReadBuffer(byte[] mIncomingMessageBuffer) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "ReadData"), true);
        fos.write(mIncomingMessageBuffer);
        fos.close();
        MessageObj mIncomingMessage = new MessageObj();
        MessageHeader mIncomingMessageHeader = new MessageHeader();
        try {
            mIncomingMessage = mIncomingMessage.parseXml(mIncomingMessageBuffer);
            mIncomingMessageHeader = mIncomingMessage.getDataHeader();
            String messageSubType = mIncomingMessageHeader.getSubType();
            Log.m2355i(LOG_TAG, "parseReadBuffer(),  mIncomingMessage is " + mIncomingMessage.getDataBody().toString(), new Object[0]);
            Log.m2355i(LOG_TAG, "parseReadBuffer(),  mIncomingMessageHeader is " + mIncomingMessageHeader.toString(), new Object[0]);
            if (messageSubType.equals(MessageObj.SUBTYPE_BLOCK)) {
                addBlockList(mIncomingMessage);
            } else if (messageSubType.equals(MessageObj.SUBTYPE_SMS)) {
                sendSMS(mIncomingMessage);
            } else if (messageSubType.equals(MessageObj.SUBTYPE_MISSED_CALL)) {
                updateMissedCallCountToZero();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    void sendSMS(MessageObj smsMessage) {
        Log.m2355i(LOG_TAG, "sendSmsMessage(),  notiMessageId=" + smsMessage.getDataHeader().getMsgId(), new Object[0]);
        String address = ((SmsMessageBody) smsMessage.getDataBody()).getNumber();
        String message = smsMessage.getDataBody().getContent();
        if (message == null) {
            message = "\n";
        }
        if (message.equals("")) {
            message = "\n";
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(SmsController.MESSAGE_STATUS_SEND_ACTION);
        sendIntent.putExtra("ADDRESS", address);
        sendIntent.putExtra("MESSAGE", message);
        sContext.sendBroadcast(sendIntent);
    }

    @SuppressLint({"InlinedApi"})
    private void updateMissedCallCountToZero() {
        ContentValues values = new ContentValues();
        values.put("new", Integer.valueOf(0));
        if (VERSION.SDK_INT >= 14) {
            values.put("is_read", Integer.valueOf(1));
        }
        StringBuilder where = new StringBuilder();
        where.append("new");
        where.append(" = 1 AND ");
        where.append(MapConstants.TYPE);
        where.append(" = ?");
        sContext.getContentResolver().update(Calls.CONTENT_URI, values, where.toString(), new String[]{Integer.toString(3)});
    }

    private void addBlockList(MessageObj blockMessage) {
        CharSequence appPackageName = (CharSequence) AppList.getInstance().getAppList().get(Integer.valueOf(Integer.parseInt(((NotificationMessageBody) blockMessage.getDataBody()).getAppID())));
        Log.m2355i(LOG_TAG, "addBlockList() appPackageName is :" + appPackageName, new Object[0]);
        HashSet<CharSequence> blockList = BlockList.getInstance().getBlockList();
        if (!blockList.contains(appPackageName) && appPackageName != null) {
            blockList.add(appPackageName);
            BlockList.getInstance().saveBlockList(blockList);
        }
    }

    void sendReadMissedCallData() {
        MessageHeader header = new MessageHeader();
        header.setCategory("call");
        header.setSubType(MessageObj.SUBTYPE_MISSED_CALL);
        header.setMsgId(Util.genMessageId());
        header.setAction(MessageObj.ACTION_ADD);
        String phoneNum = "";
        String sender = "";
        String content = "";
        int timestamp = Util.getUtcTime(Calendar.getInstance().getTimeInMillis());
        CallMessageBody body = new CallMessageBody();
        body.setSender(sender);
        body.setNumber(phoneNum);
        body.setContent(content);
        body.setMissedCallCount(0);
        body.setTimestamp(timestamp);
        Log.m2355i(LOG_TAG, "sendReadMissedCallData() sender:phoneNum:content" + sender + phoneNum + content, new Object[0]);
        MessageObj callMessageData = new MessageObj();
        callMessageData.setDataHeader(header);
        callMessageData.setDataBody(body);
        getInstance().sendCallMessage(callMessageData);
    }

    private int getMissedCallCount() {
        StringBuilder queryStr = new StringBuilder("type = ");
        queryStr.append(3);
        queryStr.append(" AND new = 1");
        Log.m2355i(LOG_TAG, "getMissedCallCount(), query string=" + queryStr, new Object[0]);
        int missedCallCount = 0;
        Cursor cur = sContext.getContentResolver().query(Calls.CONTENT_URI, new String[]{MapConstants._ID}, queryStr.toString(), null, MapConstants.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }
        Log.m2355i(LOG_TAG, "getMissedCallCount(), missed call count=" + missedCallCount, new Object[0]);
        return missedCallCount;
    }
}
