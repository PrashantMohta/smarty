package com.mtk.data;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public abstract class MessageBody {
    private String mContent = null;
    private String mSender = null;
    private int mTimestamp = 0;

    public abstract void genXmlBuff(XmlSerializer xmlSerializer) throws IllegalArgumentException, IllegalStateException, IOException;

    String getSender() {
        return this.mSender;
    }

    public void setSender(String sender) {
        this.mSender = sender;
    }

    int getTimestamp() {
        return this.mTimestamp;
    }

    public void setTimestamp(int timestamp) {
        this.mTimestamp = timestamp;
    }

    public String getContent() {
        return this.mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }
}
