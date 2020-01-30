package com.mtk.data;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class MessageHeader {
    private String mAction = null;
    private String mCategory = null;
    private int mMsgId = 0;
    private String mSubType = null;

    public void setCategory(String category) {
        this.mCategory = category;
    }

    public void setSubType(String subType) {
        this.mSubType = subType;
    }

    public void setMsgId(int msgId) {
        this.mMsgId = msgId;
    }

    public void setAction(String action) {
        this.mAction = action;
    }

    String getCategory() {
        return this.mCategory;
    }

    public String getSubType() {
        return this.mSubType;
    }

    public int getMsgId() {
        return this.mMsgId;
    }

    String getAction() {
        return this.mAction;
    }

    public void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException, NoDataException {
        if (getCategory() == null || getSubType() == null || getMsgId() == 0 || getAction() == null) {
            throw new NoDataException();
        }
        serializer.startTag(null, "header");
        serializer.startTag(null, MessageObj.CATEGORY);
        serializer.text(getCategory());
        serializer.endTag(null, MessageObj.CATEGORY);
        serializer.startTag(null, MessageObj.SUBTYPE);
        serializer.text(getSubType());
        serializer.endTag(null, MessageObj.SUBTYPE);
        serializer.startTag(null, MessageObj.MSGID);
        serializer.text(String.valueOf(getMsgId()));
        serializer.endTag(null, MessageObj.MSGID);
        serializer.startTag(null, MessageObj.ACTION);
        serializer.text(getAction());
        serializer.endTag(null, MessageObj.ACTION);
        serializer.endTag(null, "header");
    }

    public String toString() {
        return getCategory() + getSubType() + getMsgId() + getAction();
    }
}
