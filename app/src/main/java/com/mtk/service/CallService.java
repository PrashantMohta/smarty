package com.mtk.service;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog.Calls;
import android.telephony.PhoneStateListener;

import com.dandy.smartwatch.modded.R;
import com.mtk.data.CallMessageBody;
import com.mtk.data.Log;
import com.mtk.data.MessageHeader;
import com.mtk.data.MessageObj;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.mtk.map.BMessage;
import com.mtk.map.MapConstants;
import com.mtk.util.ToastUtils;

public class CallService extends PhoneStateListener {
    private static final String LOG_TAG = "CallService";
    private Context mContext = null;
    private String mIncomingNumber = null;
    private int mLastState = 0;

    public CallService(Context context) {
        Log.m2355i(LOG_TAG, "CallService(), CallService created!", new Object[0]);
        this.mContext = context;
    }

    public void onCallStateChanged(int state, String incomingNumber) {
        Log.m2355i(LOG_TAG, "onCallStateChanged(), incomingNumber" + incomingNumber, new Object[0]);
        if (this.mLastState == 1 && state == 0) {
            this.mIncomingNumber = incomingNumber;
            boolean isServiceEnabled = PreferenceData.isSmsServiceEnable();
            boolean needForward = PreferenceData.isNeedPush();
            if (isServiceEnabled && needForward) {
                sendCallMessage();
            }
        }
        this.mLastState = state;
    }

    private void sendCallMessage() {
        MessageObj callMessageData = new MessageObj();
        callMessageData.setDataHeader(createCallHeader());
        callMessageData.setDataBody(createCallBody());
        Log.m2355i(LOG_TAG, "sendCallMessage(), callMessageData=" + callMessageData, new Object[0]);
        MainService service = MainService.getInstance();
        if (service != null) {
            service.sendCallMessage(callMessageData);
        }
    }

    private MessageHeader createCallHeader() {
        MessageHeader header = new MessageHeader();
        header.setCategory("call");
        header.setSubType(MessageObj.SUBTYPE_MISSED_CALL);
        header.setMsgId(Util.genMessageId());
        header.setAction(MessageObj.ACTION_ADD);
        Log.m2355i(LOG_TAG, "createCallHeader(), header=" + header, new Object[0]);
        return header;
    }

    private CallMessageBody createCallBody() {
        String phoneNum = this.mIncomingNumber;
        String sender = Util.getContactName(this.mContext, phoneNum);
        String content = getMessageContent(sender);
        int timestamp = Util.getUtcTime(System.currentTimeMillis());
        int missedCallCount = getMissedCallCount();
        CallMessageBody body = new CallMessageBody();
        body.setSender(sender);
        body.setNumber(phoneNum);
        body.setContent(content);
        body.setMissedCallCount(missedCallCount);
        body.setTimestamp(timestamp);
        Log.m2355i(LOG_TAG, "createCallBody(), body=" + body, new Object[0]);
        return body;
    }

    private String getMessageContent(String sender) {
        StringBuilder content = new StringBuilder();
        content.append(this.mContext.getText(R.string.missed_call));
        content.append(": ");
        content.append(sender);
        content.append(BMessage.CRLF);
        content.append("Missed Call Count:");
        content.append(getMissedCallCount());
        Log.m2355i(LOG_TAG, "getMessageContent(), content=" + content, new Object[0]);
        return content.toString();
    }

    private int getMissedCallCount() {
        StringBuilder queryStr = new StringBuilder("type = ");
        queryStr.append(3);
        queryStr.append(" AND new = 1");
        Log.m2355i(LOG_TAG, "getMissedCallCount(), query string=" + queryStr, new Object[0]);
        int missedCallCount = 0;
        Cursor cur = this.mContext.getContentResolver().query(Calls.CONTENT_URI, new String[]{MapConstants._ID}, queryStr.toString(), null, MapConstants.DEFAULT_SORT_ORDER);
        if (cur != null) {
            missedCallCount = cur.getCount();
            cur.close();
        }
        Log.m2355i(LOG_TAG, "getMissedCallCount(), missed call count=" + missedCallCount, new Object[0]);
        return missedCallCount;
    }
}
