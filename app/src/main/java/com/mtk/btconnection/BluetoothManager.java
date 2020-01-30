package com.mtk.btconnection;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import com.mtk.UI.Smartwatch;
import com.mtk.data.Log;
import com.mtk.data.Util;
import com.mtk.map.BMessage;
import com.mtk.map.MapConstants;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BluetoothManager {
    public static final int BLOCKED = 1;
    public static final int BLUETOOTH_CONNECT_SUCCESS = 0;
    public static final int BLUETOOTH_NOT_CONNECT = -3;
    public static final int BLUETOOTH_NOT_ENABLE = -2;
    public static final int BLUETOOTH_NOT_SUPPORT = -1;
    public static final String BT_BROADCAST_ACTION = "com.mtk.connection.BT_CONNECTION_CHANGED";
    public static final String DEVICE_NAME = "device_name";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int FAILED = -1;
    private static final String LOG_TAG = "BluetoothManager";
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_RING = 6;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_WRITE = 3;
    public static final int READ_CMD = 2;
    public static final int READ_DATA = 3;
    public static final int READ_IDLE = 0;
    public static final int READ_PRE = 1;
    public static final int SUCCESS = 0;
    public static final String TOAST = "toast";
    public static final int TYPE_BT_CONNECTED = 1;
    public static final int TYPE_BT_CONNECTION_LOST = 2;
    public static final int TYPE_DATA_ARRIVE = 4;
    public static final int TYPE_DATA_SENT = 3;
    public static final int TYPE_MAPCMD_ARRIVE = 5;
    public static int cmdBufferLenth = 0;
    public static byte[] commandBuffer = null;
    public static byte[] dataBuffer = null;
    public static int dataBufferLenth = 0;
    private static boolean isHandshake = false;
    private static boolean isOlderThanVersionTow = true;
    public static final byte[] reciveBuffer = new byte[51200];
    public static int reciveBufferLenth = 0;
    public int CMD_TYPE = 1;
    public int READBUFFERSTATE = 0;
    private final BroadcastReceiver mBTReceiver = new C05581();
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothConnection mBluetoothConnection = null;
    private String mConnectedDeviceName = null;
    private Context mContext = null;
    private MessageHandler mHandler = null;
    private LoadJniFunction mLoadJniFunction = null;
    private MessageDataList mMessageDataList = null;
    public Timer mTimer = new Timer(true);

    class C05581 extends BroadcastReceiver {
        C05581() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.STATE_CHANGED".equals(intent.getAction())) {
                int connectionState = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", 10);
                Log.m2355i(BluetoothManager.LOG_TAG, "onReceive(), action=" + intent.getAction(), new Object[0]);
                if (connectionState == 12) {
                    BluetoothManager.this.setupConnection();
                } else if (connectionState == 10) {
                    BluetoothManager.this.removeConnection();
                }
            }
        }
    }

    class C05592 extends TimerTask {
        C05592() {
        }

        public void run() {
            Log.m2355i(BluetoothManager.LOG_TAG, "Timer Task Run ... isHandshake = " + BluetoothManager.isHandshake, new Object[0]);
            if (BluetoothManager.isOlderThanVersionTow) {
                BluetoothManager.isHandshake = true;
                BluetoothManager.this.sendBroadcast(1, null);
                BluetoothManager.this.sendDataFromFile();
                cancel();
                Log.m2355i(BluetoothManager.LOG_TAG, "mTimer is canceled verstion is old", new Object[0]);
                return;
            }
            try {
                BluetoothManager.this.sendSyncTime();
                cancel();
                Log.m2355i(BluetoothManager.LOG_TAG, "mTimer is canceled verstion is new", new Object[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    private static final class MessageHandler extends Handler {
        private WeakReference<BluetoothManager> mBluetoothManager;

        public MessageHandler(BluetoothManager bluetoothManager) {
            this.mBluetoothManager = new WeakReference(bluetoothManager);
        }

        public void handleMessage(Message msg) {
            Log.m2355i(BluetoothManager.LOG_TAG, "handleMessage(), msg.what=" + msg.what, new Object[0]);
            BluetoothManager bluetoothManager = (BluetoothManager) this.mBluetoothManager.get();
            switch (msg.what) {
                case 1:
                    Log.m2355i(BluetoothManager.LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1, new Object[0]);
                    switch (msg.arg1) {
                        case 0:
                        case 1:
                        case 2:
                            return;
                        case 3:
                            bluetoothManager.runningSyncTimer();
                            return;
                        case 4:
                            bluetoothManager.sendBroadcast(2, null);
                            BluetoothManager.cmdBufferLenth = 0;
                            BluetoothManager.reciveBufferLenth = 0;
                            BluetoothManager.dataBufferLenth = 0;
                            BluetoothManager.isHandshake = false;
                            BluetoothManager.isOlderThanVersionTow = true;
                            return;
                        default:
                            return;
                    }
                case 2:
                    byte[] readBuf = (byte[]) msg.obj;
                    int bytes = msg.arg1;
                    for (int i = 0; i < bytes; i++) {
                        Log.m2353e(BluetoothManager.LOG_TAG, BluetoothManager.LOG_TAG + i + BMessage.SEPRATOR + readBuf[i], new Object[0]);
                    }
                    System.arraycopy(readBuf, 0, BluetoothManager.reciveBuffer, BluetoothManager.reciveBufferLenth, bytes);
                    BluetoothManager.reciveBufferLenth += bytes;
                    Log.m2355i(BluetoothManager.LOG_TAG, "reciveBufferLenth is " + BluetoothManager.reciveBufferLenth, new Object[0]);
                    bluetoothManager.runningReadFSM();
                    return;
                case 3:
                    bluetoothManager.sendBroadcast(3, null);
                    return;
                case 4:
                    bluetoothManager.setConnectedDeviceName(msg.getData().getString("device_name"));
                    return;
                case 6:
                    Log.m2353e(BluetoothManager.LOG_TAG, "MESSAGE_RING is startPhoneRing()", new Object[0]);
                    BluetoothManager.startPhoneRing();
                    return;
                default:
                    return;
            }
        }
    }

    public interface setBlueToothStateListener {
        void BlueToothSateChanged(int i);
    }

    public BluetoothManager(Context context) {
        Log.m2355i(LOG_TAG, "BluetoothManager(), BluetoothManager created!", new Object[0]);
        this.mHandler = new MessageHandler(this);
        this.mContext = context;
        this.mLoadJniFunction = new LoadJniFunction();
        this.mContext.registerReceiver(this.mBTReceiver, new IntentFilter("android.bluetooth.adapter.action.STATE_CHANGED"));
        this.mMessageDataList = new MessageDataList(this.mContext);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.m2355i(LOG_TAG, " function done!", new Object[0]);

    }

    public static void startPhoneRing() {
        MainService.getInstance().startRingService();
    }

    public int setupConnection() {
        Log.m2351d(LOG_TAG, "setupConnection()", new Object[0]);
        if (this.mBluetoothAdapter == null) {
            return -1;
        }
        if (!this.mBluetoothAdapter.isEnabled()) {
            return -2;
        }
        this.mBluetoothConnection = new BluetoothConnection(this.mHandler);
        this.mBluetoothConnection.startAccept();
        Log.m2351d(LOG_TAG, "setupConnection(), setupConnection successfully!", new Object[0]);
        return 0;
    }

    public String getConnectedDeviceName() {
        Log.m2355i(LOG_TAG, "getConnectedDeviceName(), mConnectedDeviceName=" + this.mConnectedDeviceName, new Object[0]);
        return this.mConnectedDeviceName;
    }

    public void setConnectedDeviceName(String connectedDeviceName) {
        Log.m2355i(LOG_TAG, "setConnectedDeviceName(), deviceName=" + this.mConnectedDeviceName, new Object[0]);
        this.mConnectedDeviceName = connectedDeviceName;
    }

    public int removeConnection() {
        Log.m2355i(LOG_TAG, "removeConnection(), Bluetooth connection is removed!", new Object[0]);
        if (this.mBluetoothConnection != null) {
            this.mBluetoothConnection.stop();
        }
        return 0;
    }

    public boolean isBTConnected() {
        boolean isConnected;
        if (this.mBluetoothConnection != null && isHandshake && this.mBluetoothConnection.getState() == 3) {
            isConnected = true;
        } else {
            isConnected = false;
        }
        Log.m2355i(LOG_TAG, "isBTConnected(), isConnected=" + isConnected, new Object[0]);
        return isConnected;
    }

    public boolean sendData(byte[] data) {
        boolean isDataSent = false;
        if (data == null || !isBTConnected() || RemoteCameraService.isLaunched) {
            this.mMessageDataList.saveMessageData(data);
        } else {
            sendDataToRemote(1, data);
            isDataSent = true;
            if (this.mMessageDataList.getMessageDataList().size() > 0) {
                sendDataFromFile();
                Log.m2355i(LOG_TAG, "Notice!!!!, has message data not been sent.", new Object[0]);
            }
        }
        Log.m2355i(LOG_TAG, "sendData(), isDataSent=" + isDataSent, new Object[0]);
        return isDataSent;
    }

    public boolean sendMAPData(byte[] data) {
        if (!isBTConnected() || RemoteCameraService.isLaunched) {
            Log.m2355i(LOG_TAG, "sendMAPData(), isDataSent=false", new Object[0]);
            return false;
        }
        this.mBluetoothConnection.write(data);
        Log.m2355i(LOG_TAG, "sendMAPData(), isDataSent=true", new Object[0]);
        return true;
    }

    public boolean sendCAPCData(byte[] data) {
        if (isBTConnected()) {
            this.mBluetoothConnection.write(data);
            Log.m2355i(LOG_TAG, "sendCAPCData(), isDataSent=true", new Object[0]);
            return true;
        }
        Log.m2355i(LOG_TAG, "sendCAPCData(), isDataSent=false", new Object[0]);
        return false;
    }

    public boolean sendMREEData(byte[] data) {
        if (isBTConnected()) {
            this.mBluetoothConnection.write(data);
            Log.m2355i(LOG_TAG, "sendMREEData(), isDataSent=true", new Object[0]);
            return true;
        }
        Log.m2355i(LOG_TAG, "sendMREEData(), isDataSent=false", new Object[0]);
        return false;
    }

    public boolean sendDataFromFile() {
        List<byte[]> messageList = this.mMessageDataList.getMessageDataList();
        Log.m2355i(LOG_TAG, "sendDataFromFile(), message count=" + messageList.size(), new Object[0]);
        if (messageList.size() > 0) {
            int messageCount = messageList.size();
            for (int index = 0; index < messageCount && messageList.get(0) != null && isBTConnected(); index++) {
                sendDataToRemote(1, (byte[]) messageList.get(0));
                messageList.remove(0);
                Log.m2355i(LOG_TAG, "sendDataFromFile(), message index=" + index, new Object[0]);
            }
        }
        return false;
    }

    private void sendDataToRemote(int i, byte[] data) {
        if (data.length == 0) {
            Log.m2355i(LOG_TAG, "sendDataToRemote cmd and data()dataLengthis0", new Object[0]);
        }
        Log.m2355i(LOG_TAG, "sendDataToRemote cmd and data()" + getCmdBuffer(i, String.valueOf(data.length)), new Object[0]);
        this.mBluetoothConnection.write(getCmdBuffer(i, String.valueOf(data.length)));
        this.mBluetoothConnection.write(data);
        Log.m2355i(LOG_TAG, "sendDataToRemote cmd and data() String" + String.valueOf(data), new Object[0]);
    }

    public void sendMapResult(String result) {
        if (isBTConnected() && !RemoteCameraService.isLaunched) {
            sendCommandToRemote(5, result);
        }
    }

    public void sendMapDResult(String result) {
        if (isBTConnected() && !RemoteCameraService.isLaunched) {
            sendCommandToRemote(6, result);
        }
    }

    public void sendCAPCResult(String result) {
        if (isBTConnected()) {
            sendCommandToRemote(7, result);
        }
    }

    public void sendMREEResult(String result) {
        if (isBTConnected()) {
            sendCommandToRemote(8, result);
        }
    }

    private void sendCommandToRemote(int i, String command) {
        Log.m2355i(LOG_TAG, "Send Command to Remote: " + command, new Object[0]);
        this.mBluetoothConnection.write(getCmdBuffer(i, command));
    }

    private byte[] getCmdBuffer(int i, String bufferString) {
        return this.mLoadJniFunction.getDataCmd(i, bufferString);
    }

    public void saveData() {
        Log.m2355i(LOG_TAG, "saveData()", new Object[0]);
        this.mMessageDataList.saveMessageDataList();
    }

    private void sendBroadcast(int extraType, byte[] extraData) {
        Log.m2355i(LOG_TAG, "sendBroadcast(), extraType=" + extraType, new Object[0]);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BT_BROADCAST_ACTION);
        broadcastIntent.putExtra(EXTRA_TYPE, extraType);
        if (extraData != null) {
            broadcastIntent.putExtra("EXTRA_DATA", extraData);
        }
        this.mContext.sendBroadcast(broadcastIntent);
    }

    private void sendSyncTime() throws IOException {
        long curr_system_time = System.currentTimeMillis();
        sendCommandToRemote(2, String.valueOf(Util.getUtcTime(curr_system_time)) + " " + String.valueOf(Util.getUtcTimeZone(curr_system_time)));
        Log.m2355i(LOG_TAG, "sendSyncTime()", new Object[0]);
    }

    private void runningSyncTimer() {
        this.mTimer.schedule(new C05592(), 3000);
    }

    private void runningReadFSM() {
        Log.m2355i(LOG_TAG, "runningReadFSM() READBUFFERSTATE = " + this.READBUFFERSTATE, new Object[0]);
        switch (this.READBUFFERSTATE) {
            case 0:
                getCommandLenth();
                return;
            case 1:
                getCmdAndDataLenth();
                return;
            case 2:
                getData();
                return;
            default:
                return;
        }
    }

    private void getCommandLenth() {
        if (this.READBUFFERSTATE == 0) {
            int cmdpos = -1;
            if (reciveBufferLenth < 8) {
                Log.m2355i(LOG_TAG, "getCommandLenth(): reciveBufferLenth < Constants.NOTIFYMINIHEADERLENTH", new Object[0]);
                return;
            }
            int i = 0;
            while (i < reciveBufferLenth - 4) {
                if (reciveBuffer[i] == (byte) -16 && reciveBuffer[i + 1] == (byte) -16 && reciveBuffer[i + 2] == (byte) -16 && reciveBuffer[i + 3] == (byte) -15) {
                    cmdpos = i;
                    Log.m2355i(LOG_TAG, "getCommandLenth(): Get F0F0F0F1 Success", new Object[0]);
                    break;
                }
                i++;
            }
            if (cmdpos != -1) {
                cmdBufferLenth = (((reciveBuffer[i + 4] << 24) | (reciveBuffer[i + 5] << 16)) | (reciveBuffer[i + 6] << 8)) | reciveBuffer[i + 7];
                System.arraycopy(reciveBuffer, 8, reciveBuffer, 0, reciveBufferLenth - 8);
                reciveBufferLenth -= 8;
                this.READBUFFERSTATE = 1;
                Log.m2355i(LOG_TAG, "getCommandLenth(): Get cmdBufferLenth Success cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth, new Object[0]);
                runningReadFSM();
                return;
            }
            System.arraycopy(reciveBuffer, 8, reciveBuffer, 0, reciveBufferLenth - 8);
            reciveBufferLenth -= 8;
            this.READBUFFERSTATE = 0;
            Log.m2355i(LOG_TAG, "getCommandLenth(): Get cmdBufferLenth Success cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth, new Object[0]);
            runningReadFSM();
        }
    }

    private void getCmdAndDataLenth() {
        if (reciveBufferLenth < cmdBufferLenth) {
            Log.m2355i(LOG_TAG, "getDataLenth():reciveBufferLenth < cmdBufferLenth", new Object[0]);
            return;
        }
        commandBuffer = new byte[cmdBufferLenth];
        System.arraycopy(reciveBuffer, 0, commandBuffer, 0, cmdBufferLenth);
        System.arraycopy(reciveBuffer, cmdBufferLenth, reciveBuffer, 0, reciveBufferLenth - cmdBufferLenth);
        reciveBuffer[reciveBufferLenth - cmdBufferLenth] = (byte) 0;
        reciveBufferLenth -= cmdBufferLenth;
        Log.m2355i(LOG_TAG, "getDataLenth() :Get cmdBuffer Success cmdBufferLenth is " + cmdBufferLenth + "reciveBufferLenth is " + reciveBufferLenth, new Object[0]);
        this.CMD_TYPE = this.mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth);
        Log.m2355i(LOG_TAG, "Get data Success and the CMD_TYPE is " + this.CMD_TYPE, new Object[0]);
        if (isBTConnected()) {
            if (this.CMD_TYPE == 1 || this.CMD_TYPE == 5 || this.CMD_TYPE == 6 || this.CMD_TYPE == 7) {
                dataBufferLenth = this.mLoadJniFunction.getDataLenth(commandBuffer, cmdBufferLenth);
                Log.m2355i(LOG_TAG, "getDataLenth():Get dataBufferLenth Success dataBufferLenth is " + dataBufferLenth, new Object[0]);
                if (dataBufferLenth == -1) {
                    this.READBUFFERSTATE = 0;
                    return;
                }
                this.READBUFFERSTATE = 2;
                runningReadFSM();
                return;
            }
            this.READBUFFERSTATE = 0;
        } else if (this.mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth) == 3) {
            isHandshake = true;
            Log.m2355i(LOG_TAG, "isHandshake = true", new Object[0]);
            sendBroadcast(1, null);
            sendDataFromFile();
            this.READBUFFERSTATE = 0;
            runningReadFSM();
        } else if (this.mLoadJniFunction.getCmdType(commandBuffer, cmdBufferLenth) == 4) {
            reciveBuffer[0] = (byte) 0;
            reciveBufferLenth = 0;
            isOlderThanVersionTow = false;
            this.READBUFFERSTATE = 0;
            Log.m2355i(LOG_TAG, "getDataLenth():Get the Version Success", new Object[0]);
        } else {
            this.READBUFFERSTATE = 0;
        }
    }

    private void getData() {
        if (dataBufferLenth <= reciveBufferLenth) {
            dataBuffer = new byte[dataBufferLenth];
            System.arraycopy(reciveBuffer, 0, dataBuffer, 0, dataBufferLenth);
            System.arraycopy(reciveBuffer, dataBufferLenth, reciveBuffer, 0, reciveBufferLenth - dataBufferLenth);
            reciveBuffer[reciveBufferLenth - dataBufferLenth] = (byte) 0;
            reciveBufferLenth -= dataBufferLenth;
            this.READBUFFERSTATE = 0;
            cmdBufferLenth = 0;
            dataBufferLenth = 0;
            if (this.CMD_TYPE == 1) {
                sendBroadcast(4, dataBuffer);
            } else if (this.CMD_TYPE == 5 || this.CMD_TYPE == 6) {
                Log.m2355i(LOG_TAG, "sendBroadcast of MAPX OR MAPD :" + this.CMD_TYPE, new Object[0]);
                Log.m2355i(LOG_TAG, "mIsNeedStartBTMapService is :true", new Object[0]);
                sendBroadcasetToMapService(dataBuffer);
            } else if (this.CMD_TYPE == 7) {
                sendBroadcasetToCapService(dataBuffer);
            }
            Log.m2355i(LOG_TAG, "reciveBufferLenth is " + reciveBufferLenth, new Object[0]);
            if (reciveBufferLenth != 0) {
                runningReadFSM();
            }
        }
    }

    public void sendBroadcasetToMapService(byte[] dataBuffer) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MapConstants.BT_MAP_BROADCAST_ACTION);
        if (dataBuffer != null) {
            broadcastIntent.putExtra("EXTRA_DATA", dataBuffer);
        }
        this.mContext.sendBroadcast(broadcastIntent);
    }

    public void sendBroadcasetToCapService(byte[] dataBuffer) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(RemoteCameraService.BT_REMOTECAMERA_BROADCAST_ACTION);
        if (dataBuffer != null) {
            broadcastIntent.putExtra("EXTRA_DATA", dataBuffer);
        }
        this.mContext.sendBroadcast(broadcastIntent);
    }
}
