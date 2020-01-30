package com.mtk.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.PreferenceData;
import com.mtk.data.SmsMessageBody;
import com.mtk.data.Util;

class SmsService extends BroadcastReceiver {
    private static final String LOG_TAG = "SmsService";
    public static final String SMS_ACTION = "SenderSMSFromeFP";
    private static final String SMS_RECEIVED = "com.mtk.btnotification.SMS_RECEIVED";
    private static String preID = null;
    private Context mContext = null;

    void sendSms() {
        /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r10 = this;
        r8 = 0;
        r0 = r10.mContext;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = r0.getContentResolver();	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r1 = "content://sms/inbox";	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r1 = android.net.Uri.parse(r1);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r2 = 0;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r3 = 0;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r4 = 0;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r5 = "_id desc";	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r8 = r0.query(r1, r2, r3, r4, r5);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        if (r8 == 0) goto L_0x0044;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
    L_0x0018:
        r0 = r8.moveToNext();	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        if (r0 == 0) goto L_0x0044;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
    L_0x001e:
        r0 = "body";	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = r8.getColumnIndex(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r9 = r8.getString(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = "address";	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = r8.getColumnIndex(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r7 = r8.getString(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = "_id";	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = r8.getColumnIndex(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r6 = r8.getString(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = preID;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        r0 = r6.equals(r0);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        if (r0 == 0) goto L_0x004a;
    L_0x0044:
        if (r8 == 0) goto L_0x0049;
    L_0x0046:
        r8.close();
    L_0x0049:
        return;
    L_0x004a:
        preID = r6;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        if (r9 == 0) goto L_0x0018;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
    L_0x004e:
        if (r7 == 0) goto L_0x0018;	 Catch:{ Exception -> 0x0054, all -> 0x005b }
    L_0x0050:
        r10.sendSmsMessage(r9, r7);	 Catch:{ Exception -> 0x0054, all -> 0x005b }
        goto L_0x0044;
    L_0x0054:
        r0 = move-exception;
        if (r8 == 0) goto L_0x0049;
    L_0x0057:
        r8.close();
        goto L_0x0049;
    L_0x005b:
        r0 = move-exception;
        if (r8 == 0) goto L_0x0061;
    L_0x005e:
        r8.close();
    L_0x0061:
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.mtk.service.SmsService.sendSms():void");
    }

    public SmsService() {
        Log.m2355i(LOG_TAG, "SmsReceiver(), SmsReceiver created!", new Object[0]);
    }

    public void onReceive(Context context, Intent intent) {
        Log.m2355i(LOG_TAG, "onReceive()", new Object[0]);
        boolean isServiceEnabled = PreferenceData.isSmsServiceEnable();
        boolean needForward = PreferenceData.isNeedPush();
        if (isServiceEnabled && needForward) {
            this.mContext = context;
            if (intent.getAction().equals(SMS_RECEIVED)) {
                sendSms();
            }
        }
    }

    private void sendSmsMessage(String msgbody, String address) {
        MessageObj smsMessageData = new MessageObj();
        smsMessageData.setDataHeader(createSmsHeader());
        smsMessageData.setDataBody(createSmsBody(address, msgbody));
        Log.m2355i(LOG_TAG, "sendSmsMessage(), smsMessageData=" + smsMessageData, new Object[0]);
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendSmsMessage(smsMessageData);
        }
    }

    private MessageHeader createSmsHeader() {
        MessageHeader header = new MessageHeader();
        header.setCategory(MessageObj.CATEGORY_NOTI);
        header.setSubType(MessageObj.SUBTYPE_SMS);
        header.setMsgId(Util.genMessageId());
        header.setAction(MessageObj.ACTION_ADD);
        Log.m2355i(LOG_TAG, "createSmsHeader(), header=" + header, new Object[0]);
        return header;
    }

    private SmsMessageBody createSmsBody(String address, String msgbody) {
        String phoneNum = address;
        String sender = Util.getContactName(this.mContext, phoneNum);
        String content = msgbody;
        int timestamp = Util.getUtcTime(System.currentTimeMillis());
        SmsMessageBody body = new SmsMessageBody();
        body.setSender(sender);
        body.setNumber(phoneNum);
        body.setContent(content);
        body.setTimestamp(timestamp);
        Log.m2355i(LOG_TAG, "createSmsBody(), body=" + body, new Object[0]);
        return body;
    }
}
