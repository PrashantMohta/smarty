package com.mtk.data;

import android.content.Context;
import com.mtk.BTNotificationApplication;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

public final class BlockList {
    private static final BlockList INSTANCE = new BlockList();
    private static final String LOG_TAG = "BlockList";
    private static final String SAVE_FILE_NAME = "BlockList";
    private HashSet<CharSequence> mBlockList = null;
    private Context mContext = null;

    private BlockList() {
        Log.m2355i("BlockList", "BlockList(), BlockList created!", new Object[0]);
        this.mContext = BTNotificationApplication.getInstance().getApplicationContext();
    }

    public static BlockList getInstance() {
        return INSTANCE;
    }

    public HashSet<CharSequence> getBlockList() {
        if (this.mBlockList == null) {
            loadBlockListFromFile();
        }
        Log.m2355i("BlockList", "getBlockList(), mBlockList = " + this.mBlockList.toString(), new Object[0]);
        return this.mBlockList;
    }

    private void loadBlockListFromFile() {
        Log.m2355i("BlockList", "loadIgnoreListFromFile(),  file_name= BlockList", new Object[0]);
        if (this.mBlockList == null) {
            try {
                this.mBlockList = (HashSet) new ObjectInputStream(this.mContext.openFileInput("BlockList")).readObject();
            } catch (ClassNotFoundException exception) {
                exception.printStackTrace();
            } catch (IOException exception2) {
                exception2.printStackTrace();
            }
        }
        if (this.mBlockList == null) {
            this.mBlockList = new HashSet();
        }
    }

    public void saveBlockList(HashSet<CharSequence> blockList) {
        Log.m2355i("BlockList", "setIgnoreList(),  file_name= BlockList", new Object[0]);
        try {
            FileOutputStream fileoutputstream = this.mContext.openFileOutput("BlockList", 0);
            ObjectOutputStream objectoutputstream = new ObjectOutputStream(fileoutputstream);
            objectoutputstream.writeObject(blockList);
            objectoutputstream.close();
            fileoutputstream.close();
            this.mBlockList = blockList;
            Log.m2355i("BlockList", "setIgnoreList(),  mIgnoreList= " + this.mBlockList, new Object[0]);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
