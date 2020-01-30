package com.mtk.data;

import android.content.Context;
import com.mtk.BTNotificationApplication;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public final class IgnoreList {
    private static final String[] EXCLUSION_LIST = new String[]{"android", "com.android.mms", "com.android.phone", "com.android.providers.downloads", "com.android.bluetooth", "com.mediatek.bluetooth", "com.htc.music", "com.lge.music", "com.sec.android.app.music", "com.sonyericsson.music", "com.ijinshan.mguard"};
    private static final IgnoreList INSTANCE = new IgnoreList();
    private static final String LOG_TAG = "IgnoreList";
    private static final String SAVE_FILE_NAME = "IgnoreList";
    private Context mContext = null;
    private HashSet<String> mIgnoreList = null;

    private IgnoreList() {
        Log.m2355i("IgnoreList", "IgnoreList(), IgnoreList created!", new Object[0]);
        this.mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    public static IgnoreList getInstance() {
        return INSTANCE;
    }

    public HashSet<String> getIgnoreList() {
        if (this.mIgnoreList == null) {
            loadIgnoreListFromFile();
        }
        Log.m2355i("IgnoreList", "getIgnoreList(), mIgnoreList = " + this.mIgnoreList.toString(), new Object[0]);
        return this.mIgnoreList;
    }

    private void loadIgnoreListFromFile() {
        Log.m2355i("IgnoreList", "loadIgnoreListFromFile(),  file_name= IgnoreList", new Object[0]);
        if (this.mIgnoreList == null) {
            try {
                this.mIgnoreList = (HashSet) new ObjectInputStream(this.mContext.openFileInput("IgnoreList")).readObject();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception2) {
                exception2.printStackTrace();
            }
        }
        if (this.mIgnoreList == null) {
            this.mIgnoreList = new HashSet();
        }
    }

    public void saveIgnoreList(HashSet<String> ignoreList) {
        Log.m2355i("IgnoreList", "setIgnoreList(),  file_name= IgnoreList", new Object[0]);
        try {
            FileOutputStream fileoutputstream = this.mContext.openFileOutput("IgnoreList", 0);
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(ignoreList);
            objectoutputstream.close();
            fileoutputstream.close();
            this.mIgnoreList = ignoreList;
            Log.m2355i("IgnoreList", "setIgnoreList(),  mIgnoreList= " + this.mIgnoreList, new Object[0]);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public HashSet<String> getExclusionList() {
        HashSet<String> exclusionList = new HashSet();
        for (String exclusionPackage : EXCLUSION_LIST) {
            exclusionList.add(exclusionPackage);
        }
        Log.m2355i("IgnoreList", "setIgnoreList(),  exclusionList=" + exclusionList, new Object[0]);
        return exclusionList;
    }
}
