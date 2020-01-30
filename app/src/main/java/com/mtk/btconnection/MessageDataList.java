package com.mtk.btconnection;

import android.content.Context;
import com.mtk.data.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MessageDataList {
    private static final String LOG_TAG = "MessageDataList";
    private static final int MAX_MSG_COUNT = 5;
    private static final String SAVE_FILE_NAME = "MessageDataList";
    private Context mContext = null;
    private LinkedList<byte[]> mMsgList = null;

    public MessageDataList(Context context) {

        //saveMessageDataList();
        Log.m2355i("MessageDataList", "MessageList(), MessageList created!", new Object[0]);
        this.mContext = context;
        loadMessageDataList();
    }

    public void saveMessageData(byte[] msgData) {
        Log.m2355i("MessageDataList", "saveMessageData(), msgData=" + Arrays.toString(msgData), new Object[0]);
        if (this.mMsgList.size() >= 5) {
            this.mMsgList.remove(0);
        }
        this.mMsgList.add(msgData);
    }

    public List<byte[]> getMessageDataList() {
        Log.m2355i("MessageDataList", "getMessageDataList(), msgData=" + this.mMsgList, new Object[0]);
        if (this.mMsgList == null) {
            loadMessageDataList();
        }
        return this.mMsgList;
    }

    private void loadMessageDataList() {
        Log.m2355i("MessageDataList", "loadMessageDataList(),  file_name= MessageDataList", new Object[0]);
        try {
            Object obj = new ObjectInputStream(this.mContext.openFileInput("MessageDataList")).readObject();
            if (obj instanceof LinkedList) {
                this.mMsgList = (LinkedList) obj;
            }
        } catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        } catch (IOException exception2) {
            exception2.printStackTrace();
        }
        if (this.mMsgList == null) {
            this.mMsgList = new LinkedList();
        }
    }

    public void saveMessageDataList() {
        Log.m2355i("MessageDataList", "saveMessageDataList(),  file_name= MessageDataList", new Object[0]);
        if (this.mMsgList != null) {
            try {
                FileOutputStream fileoutputstream = this.mContext.openFileOutput("MessageDataList", 0);
                ObjectOutputStream objectoutputstream = new ObjectOutputStream(fileoutputstream);
                objectoutputstream.writeObject(this.mMsgList);
                objectoutputstream.close();
                fileoutputstream.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Log.m2355i("MessageDataList", "saveMessageDataList(),  mMsgList= " + this.mMsgList, new Object[0]);
        }
    }
}
