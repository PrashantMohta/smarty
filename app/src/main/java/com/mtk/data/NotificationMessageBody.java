package com.mtk.data;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;

public class NotificationMessageBody extends MessageBody {
    private String mAppId = null;
    private String mIcon = null;
    private String mTickerText = null;
    private String mTitle = null;

    String getIcon() {
        return this.mIcon;
    }

    public void setIcon(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        this.mIcon = Base64.encodeToString(baos.toByteArray(), 0);
    }

    public void genXmlBuff(XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException {
        serializer.startTag(null, "body");
        if (getSender() != null) {
            serializer.startTag(null, MessageObj.SENDER);
            serializer.text(getSender());
            serializer.endTag(null, MessageObj.SENDER);
        }
        if (getAppID() != null) {
            serializer.startTag(null, MessageObj.APPID);
            serializer.text(getAppID());
            serializer.endTag(null, MessageObj.APPID);
        }
        if (getIcon() != null) {
            serializer.startTag(null, MessageObj.ICON);
            serializer.cdsect(getIcon());
            serializer.endTag(null, MessageObj.ICON);
        }
        if (getTitle() != null) {
            serializer.startTag(null, MessageObj.TITLE);
            serializer.cdsect(getTitle());
            serializer.endTag(null, MessageObj.TITLE);
        }
        if (getContent() != null) {
            serializer.startTag(null, "content");
            serializer.cdsect(getContent());
            serializer.endTag(null, "content");
        }
        if (getTickerText() != null) {
            serializer.startTag(null, MessageObj.TICKER_TEXT);
            serializer.cdsect(getTickerText());
            serializer.endTag(null, MessageObj.TICKER_TEXT);
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
        str.append(", ");
        if (getIcon() != null) {
            str.append(getIcon());
        }
        str.append(", ");
        if (getIcon() != null) {
            str.append(getAppID());
        }
        str.append(", ");
        if (getTitle() != null) {
            str.append(getTitle());
        }
        str.append(", ");
        if (getContent() != null) {
            str.append(getContent());
        }
        str.append(", ");
        if (getTickerText() != null) {
            str.append(getTickerText());
        }
        str.append(", ");
        if (getTimestamp() != 0) {
            str.append(String.valueOf(getTimestamp()));
        }
        str.append("]");
        return str.toString();
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTickerText() {
        return this.mTickerText;
    }

    public void setTickerText(String tickerText) {
        this.mTickerText = tickerText;
    }

    public String getAppID() {
        return this.mAppId;
    }

    public void setAppID(String appID) {
        this.mAppId = appID;
    }
}
