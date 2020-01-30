package com.mtk.map;

import com.mtk.data.MessageObj;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BMessage {
    private static final String BEGINBBODY = "BEGIN:BBODY";
    private static final String BEGINBENV = "BEGIN:BENV";
    private static final String BEGINBMSG = "BEGIN:BMSG";
    private static final String BEGINMSG = "BEGIN:MSG";
    private static final String CHARSET = "CHARSET:UTF-8";
    public static final String CRLF = "\r\n";
    private static final String ENDBBODY = "END:BBODY";
    private static final String ENDBENV = "END:BENV";
    private static final String ENDBMSG = "END:BMSG";
    private static final String ENDMSG = "END:MSG";
    private static final String FOLDER = "FOLDER";
    private static final String LENGTH = "LENGTH";
    public static final String SEPRATOR = ":";
    private static final String STATUS = "STATUS";
    private static final String STATUSREAD = "READ";
    private static final String STATUSUNREAD = "UNREAD";
    private static final String TYPE = "TYPE:SMS_GSM";
    private static final String VERSION = "VERSION";
    private static final String VERSION_10 = "1.0";
    private String mBody;
    private ArrayList<Integer> mContentSize;
    private String mOrignator;
    private int mReadStatus;
    private ArrayList<String> mRecipient;
    private ArrayList<Integer> mRecipientSize;
    private long mWholeSize;

    public BMessage() {
        initCache();
    }

    private void initCache() {
        this.mRecipient = new ArrayList();
        this.mRecipientSize = new ArrayList();
        this.mContentSize = new ArrayList();
    }

    public void reset() {
        this.mReadStatus = -1;
        this.mOrignator = null;
        if (this.mRecipientSize != null) {
            this.mRecipientSize.clear();
        }
        if (this.mRecipient != null) {
            this.mRecipient.clear();
        }
        if (this.mContentSize != null) {
            this.mContentSize.clear();
        }
        this.mWholeSize = 0;
    }

    public boolean setOrignator(String orignator) {
        this.mOrignator = orignator;
        return true;
    }

    public boolean addRecipient(String recipient) {
        if (recipient != null) {
            this.mRecipientSize.add(Integer.valueOf(recipient.length()));
            this.mRecipient.add(recipient);
        }
        return true;
    }

    public boolean setContentSize(int size) {
        this.mWholeSize = (long) size;
        this.mContentSize.add(Integer.valueOf(size));
        return true;
    }

    public boolean setContentSize(File file) {
        if (file == null) {
            return false;
        }
        try {
            FileInputStream stream = new FileInputStream(file);
            int size = stream.available();
            this.mWholeSize = (long) size;
            this.mContentSize.add(Integer.valueOf(size));
            stream.close();
            return true;
        } catch (IOException e) {
            return true;
        }
    }

    public boolean setContent(String text) {
        this.mBody = text;
        return true;
    }

    public long getContentSize() {
        return this.mWholeSize;
    }

    public int getContentSize(int i) {
        if (i < this.mContentSize.size()) {
            return ((Integer) this.mContentSize.get(i)).intValue();
        }
        return 0;
    }

    String getOrignator() {
        return this.mOrignator;
    }

    public String getFinalRecipient() {
        if (this.mRecipient.size() > 0) {
            return (String) this.mRecipient.get(this.mRecipient.size() - 1);
        }
        return null;
    }

    ArrayList<String> getRecipient() {
        return this.mRecipient;
    }

    int getReadStatus() {
        return this.mReadStatus;
    }

    public void setReadStatus(int state) {
        switch (state) {
            case 0:
            case 1:
                this.mReadStatus = state;
                return;
            default:
                this.mReadStatus = 1;
                return;
        }
    }

    public String toString() {
        StringBuilder bMessageObject = new StringBuilder();
        bMessageObject.append(BEGINBMSG);
        bMessageObject.append(CRLF);
        bMessageObject.append(VERSION);
        bMessageObject.append(SEPRATOR);
        bMessageObject.append(VERSION_10);
        bMessageObject.append(CRLF);
        bMessageObject.append(STATUS);
        bMessageObject.append(SEPRATOR);
        if (getReadStatus() == 1) {
            bMessageObject.append(STATUSREAD);
        } else {
            bMessageObject.append(STATUSUNREAD);
        }
        bMessageObject.append(CRLF);
        bMessageObject.append(TYPE);
        bMessageObject.append(CRLF);
        bMessageObject.append(FOLDER);
        bMessageObject.append(SEPRATOR);
        bMessageObject.append(CRLF);
        bMessageObject.append(getOrignator());
        bMessageObject.append(CRLF);
        bMessageObject.append(BEGINBENV);
        bMessageObject.append(CRLF);
        bMessageObject.append(getRecipient());
        bMessageObject.append(CRLF);
        bMessageObject.append(BEGINBBODY);
        bMessageObject.append(CRLF);
        bMessageObject.append(CHARSET);
        bMessageObject.append(CRLF);
        bMessageObject.append(LENGTH);
        bMessageObject.append(SEPRATOR);
        try {
            if (this.mBody == null) {
                this.mBody = "\n";
                bMessageObject.append("0");
            } else {
                bMessageObject.append(String.valueOf(this.mBody.getBytes(MessageObj.CHARSET).length));
            }
        } catch (Exception e) {
        }
        bMessageObject.append(CRLF);
        bMessageObject.append(BEGINMSG);
        bMessageObject.append(CRLF);
        bMessageObject.append(this.mBody);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDMSG);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBBODY);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBENV);
        bMessageObject.append(CRLF);
        bMessageObject.append(ENDBMSG);
        return bMessageObject.toString();
    }
}
