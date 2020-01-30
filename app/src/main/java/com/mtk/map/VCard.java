package com.mtk.map;

import android.util.Log;

public class VCard {
    private static final String BEGIN = "BEGIN:VCARD";
    private static final String CRLF = "\r\n";
    private static final String EMAIL = "EMAIL";
    private static final String END = "END:VCARD";
    private static final String FORMAT_NAME = "FN";
    private static final String NAME = "N";
    private static final String SEPRATOR = ":";
    public static final String TELEPHONE = "TEL";
    private static final String VERSION = "VERSION";
    private static final String VERSION_21 = "2.1";
    private static final String VERSION_30 = "3.0";
    private String mEmail;
    private String mFormatName;
    private String mName;
    private String mTelephone;
    private String mVersion;

    public VCard(String version) {
        this.mVersion = VERSION_21;
        if (version.equals(VERSION_21) || version.equals(VERSION_30)) {
            this.mVersion = version;
        } else {
            this.mVersion = VERSION_21;
        }
    }

    public VCard() {
        this.mVersion = VERSION_21;
        this.mVersion = VERSION_21;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public void setFormatName(String name) {
        this.mFormatName = name;
    }

    public void setTelephone(String tel) {
        this.mTelephone = tel;
    }

    public void setEmail(String email) {
        this.mEmail = email;
    }

    public void reset() {
        this.mEmail = null;
        this.mTelephone = null;
        this.mFormatName = null;
        this.mName = null;
    }

    public String getName() {
        return this.mName;
    }

    public String getFormatName() {
        return this.mFormatName;
    }

    public String getTelephone() {
        return this.mTelephone;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public String toString() {
        StringBuilder vCard = new StringBuilder();
        vCard.append(BEGIN);
        vCard.append("\r\n");
        vCard.append(VERSION);
        vCard.append(":");
        vCard.append(this.mVersion);
        vCard.append("\r\n");
        vCard.append(NAME);
        vCard.append(":");
        if (this.mName != null) {
            vCard.append(this.mName);
        }
        vCard.append("\r\n");
        if (this.mVersion.equals(VERSION_30)) {
            vCard.append(FORMAT_NAME);
            vCard.append(":");
            if (this.mName != null) {
                vCard.append(this.mFormatName);
            }
            vCard.append("\r\n");
        }
        if (this.mTelephone != null) {
            vCard.append(TELEPHONE);
            vCard.append(":");
            vCard.append(this.mTelephone);
            vCard.append("\r\n");
        }
        if (this.mEmail != null) {
            vCard.append(EMAIL);
            vCard.append(":");
            vCard.append(this.mEmail);
            vCard.append("\r\n");
        }
        vCard.append(END);
        return vCard.toString();
    }

    public void parse(String vcard) {
        if (vcard != null) {
            for (String element : vcard.split("\r\n")) {
                String[] item = element.split(":");
                if (item.length >= 2) {
                    String key = item[0].trim();
                    String value = item[1].trim();
                    if (key.equals(NAME)) {
                        this.mName = value;
                    } else if (key.equals(FORMAT_NAME)) {
                        this.mFormatName = value;
                    } else if (key.equals(TELEPHONE)) {
                        this.mTelephone = value;
                    } else if (key.equals(EMAIL)) {
                        this.mEmail = value;
                    } else {
                        log("unrecognized key:" + key);
                    }
                }
            }
        }
    }

    private void log(String info) {
        if (info != null) {
            Log.v("VCard", info);
        }
    }
}
