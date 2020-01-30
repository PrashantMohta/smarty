package com.mtk.data;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class CallMessageBody extends MessageBody {
    private int mMissedCallCount = 0;
    private String mNumber = null;

    String getNumber() {
        return this.mNumber;
    }

    public void setNumber(String number) {
        this.mNumber = number;
    }

    int getMissedCallCount() {
        return this.mMissedCallCount;
    }

    public void setMissedCallCount(int missedCallCount) {
        this.mMissedCallCount = missedCallCount;
    }

    public void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, "body");
        if (getSender() != null) {
            serializer.startTag(null, MessageObj.SENDER);
            serializer.text(getSender());
            serializer.endTag(null, MessageObj.SENDER);
        }
        if (getNumber() != null) {
            serializer.startTag(null, MessageObj.NUMBER);
            serializer.text(getNumber());
            serializer.endTag(null, MessageObj.NUMBER);
        }
        if (getContent() != null) {
            serializer.startTag(null, "content");
            serializer.text(getContent());
            serializer.endTag(null, "content");
        }
        if (getMissedCallCount() >= 0) {
            serializer.startTag(null, MessageObj.MISSED_CALL_COUNT);
            serializer.text(String.valueOf(getMissedCallCount()));
            serializer.endTag(null, MessageObj.MISSED_CALL_COUNT);
        }
        if (getTimestamp() != 0) {
            serializer.startTag(null, MessageObj.TIEMSTAMP);
            serializer.text(String.valueOf(getTimestamp()));
            serializer.endTag(null, MessageObj.TIEMSTAMP);
        }
        serializer.endTag(null, "body");
    }

    public String toString() {
        String separator = ", ";
        StringBuilder str = new StringBuilder();
        str.append("[");
        if (getSender() != null) {
            str.append(getSender());
        }
        str.append(separator);
        if (getNumber() != null) {
            str.append(getNumber());
        }
        str.append(separator);
        if (getContent() != null) {
            str.append(getContent());
        }
        str.append(separator);
        if (getMissedCallCount() >= 0) {
            str.append(getMissedCallCount());
        }
        str.append(separator);
        if (getTimestamp() != 0) {
            str.append(String.valueOf(getTimestamp()));
        }
        str.append("]");
        return str.toString();
    }
}
