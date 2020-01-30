package com.mtk.map;

import android.text.format.Time;
import java.io.UnsupportedEncodingException;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public class MessageListItem {
    private int AttachSize;
    private String DateTime;
    private long MsgHandle;
    private int OrignalMsgSize;
    private String RecipientAddr;
    private String RecipientName;
    private int RecipientStatus;
    private String SenderAddr;
    private String SenderName;
    private String Subject;
    private boolean bPriority;
    private boolean bProtected;
    private boolean bText;
    private ArrayList<String> mMessageItemFeildList = null;
    private int read;

    public ArrayList<String> getMessageItem() {
        if (this.mMessageItemFeildList != null) {
            return this.mMessageItemFeildList;
        }
        this.mMessageItemFeildList = new ArrayList();
        this.mMessageItemFeildList.add(0, String.valueOf(this.MsgHandle));
        this.mMessageItemFeildList.add(1, this.Subject);
        this.mMessageItemFeildList.add(2, this.DateTime);
        this.mMessageItemFeildList.add(3, this.SenderName);
        this.mMessageItemFeildList.add(4, this.SenderAddr);
        this.mMessageItemFeildList.add(5, this.RecipientName);
        this.mMessageItemFeildList.add(6, this.RecipientAddr);
        this.mMessageItemFeildList.add(7, MapConstants.MESSAGE_TYPE_SMS_GSM);
        this.mMessageItemFeildList.add(8, String.valueOf(this.OrignalMsgSize));
        this.mMessageItemFeildList.add(9, String.valueOf(this.bText));
        this.mMessageItemFeildList.add(10, String.valueOf(this.RecipientStatus));
        this.mMessageItemFeildList.add(11, String.valueOf(this.AttachSize));
        this.mMessageItemFeildList.add(12, String.valueOf(this.bPriority));
        this.mMessageItemFeildList.add(13, String.valueOf(this.read));
        this.mMessageItemFeildList.add(14, String.valueOf(true));
        this.mMessageItemFeildList.add(15, String.valueOf(this.bProtected));
        return this.mMessageItemFeildList;
    }

    public synchronized void set(String subject, long time, String senderAddr, String sendName, String reply, String recepientName, String recepientAddr, int msgType, int origSize, boolean bText, int recepientStatus, int AttachSize, int read, boolean protect) {
        setSubject(subject);
        setDatetime(time);
        this.SenderAddr = senderAddr;
        this.SenderName = sendName;
        this.RecipientName = recepientName;
        this.RecipientAddr = recepientAddr;
        this.RecipientStatus = recepientStatus;
        this.OrignalMsgSize = origSize;
        this.bText = bText;
        this.bPriority = false;
        this.read = read;
        this.bProtected = protect;
    }

    public void setSubject(String sub) {
        if (sub != null) {
            sub = encode(sub);
            byte[] databytes = sub.getBytes();
            if (databytes.length > MapConstants.MAX_SUBJECT_LEN) {
                try {
                    this.Subject = new String(databytes, 0, 253, "utf-8");
                    return;
                } catch (UnsupportedEncodingException e) {
                    return;
                }
            }
            this.Subject = sub;
        }
    }

    public void setHandle(long handle) {
        this.MsgHandle = handle;
    }

    public void setDatetime(long millis) {
        this.DateTime = convertMillisToUtc(millis);
    }

    public void setSenderName(String name) {
        this.SenderName = encode(name);
    }

    public void setSenderAddr(String addr) {
        this.SenderAddr = addr;
    }

    public void setRecipientName(String name) {
        this.RecipientName = encode(name);
    }

    public void setRecipientAddr(String addr) {
        this.RecipientAddr = addr;
    }

    public void setRecipientStatus(int status) {
        this.RecipientStatus = status;
    }

    public void setMsgType() {
    }

    public void setSize(int size) {
        this.OrignalMsgSize = size;
    }

    public void setText(boolean text) {
        this.bText = text;
    }

    public void setAttachSize() {
        this.AttachSize = 0;
    }

    public void setPriority() {
        this.bPriority = false;
    }

    public void setReadStatus(int read) {
        this.read = read;
    }

    public void setProtected() {
        this.bProtected = false;
    }

    private String convertMillisToUtc(long millis) {
        Time mTime = new Time();
        mTime.set(millis);
        return mTime.toString().substring(0, 15);
    }

    private String encode(String rawData) {
        if (rawData == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        StringCharacterIterator iterator = new StringCharacterIterator(rawData);
        for (char character = iterator.current(); character != '￿'; character = iterator.next()) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&apos;");
            } else if (character == '&') {
                result.append("&amp;");
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}
