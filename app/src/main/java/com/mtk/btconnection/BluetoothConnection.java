package com.mtk.btconnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import com.mtk.BTNotificationApplication;
import com.mtk.data.Log;
import com.mtk.map.BMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection {
    public static final String LOG_TAG = "BluetoothConnection";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME = "BTNotification";
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECT_LOST = 4;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_NONE = 0;
    private final BluetoothAdapter mAdapter;
    private ClientThread mClientThread;
    private int mConnectState;
    private Context mContext = null;
    private final Handler mMessageHandler;
    private ServerThread mServerThread;
    private WorkThread mWorkThread;

    private class ClientThread extends Thread {
        private final BluetoothSocket mClientSocket;
        private final BluetoothDevice mRemoteDevice;

        public ClientThread(BluetoothDevice remoteDevice) {
            this.mRemoteDevice = remoteDevice;
            BluetoothSocket tmp = null;
            try {
                tmp = remoteDevice.createRfcommSocketToServiceRecord(BluetoothConnection.MY_UUID);
            } catch (IOException e) {
                Log.m2358w("create client socket failed", e);
            }
            this.mClientSocket = tmp;
        }

        public void run() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "ClientThread BEGIN", new Object[0]);
            setName("ClientThread");
            BluetoothConnection.this.mAdapter.cancelDiscovery();
            try {
                this.mClientSocket.connect();
                synchronized (BluetoothConnection.this) {
                    BluetoothConnection.this.mClientThread = null;
                }
                BluetoothConnection.this.connected(this.mClientSocket, this.mRemoteDevice);
            } catch (IOException e) {
                BluetoothConnection.this.connectionFailed();
                try {
                    this.mClientSocket.close();
                } catch (IOException e2) {
                    Log.m2358w("unable to close socket during connection failure", e2);
                }
                BluetoothConnection.this.startAccept();
            }
        }

        public void cancel() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "cancel(), ClientThread is canceled", new Object[0]);
            try {
                this.mClientSocket.close();
            } catch (IOException e) {
                Log.m2358w("close connect socket failed", e);
            }
        }
    }

    private class ServerThread extends Thread {
        private final BluetoothServerSocket mServerSocket;

        public ServerThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = BluetoothConnection.this.mAdapter.listenUsingRfcommWithServiceRecord(BluetoothConnection.NAME, BluetoothConnection.MY_UUID);
            } catch (IOException e) {
                Log.m2358w("ServerThread listen() failed", e);
            }
            this.mServerSocket = tmp;
        }

        public void run() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "ServerThread BEGIN" + this, new Object[0]);
            setName("ServerThread");
            while (BluetoothConnection.this.mConnectState != 3) {
                try {
                    BluetoothSocket socket = this.mServerSocket.accept();
                    if (socket != null) {
                        synchronized (BluetoothConnection.this) {
                            switch (BluetoothConnection.this.mConnectState) {
                                case 0:
                                case 3:
                                    try {
                                        socket.close();
                                        break;
                                    } catch (IOException e) {
                                        Log.m2358w("ServerThread Could not close unwanted socket", e);
                                        break;
                                    }
                                case 1:
                                case 2:
                                    BluetoothConnection.this.connected(socket, socket.getRemoteDevice());
                                    break;
                            }
                        }
                    }
                } catch (IOException e2) {
                    Log.m2358w("ServerThread accept() failed", e2);
                    return;
                } catch (Exception e3) {
                    Log.m2358w("mServerSocket is exception", e3);
                    return;
                }
            }
            Log.m2355i(BluetoothConnection.LOG_TAG, "ServerThread END", new Object[0]);
            return;
        }

        public void cancel() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "cancel(),  ServerThread is canceled", new Object[0]);
            try {
                this.mServerSocket.close();
            } catch (IOException e) {
                Log.m2358w("close server socket failed", e);
            } catch (Exception e2) {
                Log.m2358w("mServerSocket is exception", e2);
            }
        }
    }

    private class WorkThread extends Thread {
        private final InputStream mInStream;
        private final OutputStream mOutStream;
        private final BluetoothSocket mSocket;

        public WorkThread(BluetoothSocket socket) {
            Log.m2355i(BluetoothConnection.LOG_TAG, "WorkThread(), create WorkThread", new Object[0]);
            this.mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.m2358w("temp sockets not created", e);
            }
            this.mInStream = tmpIn;
            this.mOutStream = tmpOut;
        }

        public void run() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "WorkThread BEGIN", new Object[0]);
            while (true) {
                try {
                    byte[] buffer = new byte[5120];
                    int bytes = this.mInStream.read(buffer);
                    Log.m2353e("test", "buffer:" + buffer.toString(), new Object[0]);
                    for (int i = 0; i < bytes; i++) {
                        Log.m2355i(BluetoothConnection.LOG_TAG, "" + i + BMessage.SEPRATOR + buffer[i], new Object[0]);
                    }
                    BluetoothConnection.this.mMessageHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.m2358w("disconnected", e);
                    BluetoothConnection.this.connectionLost();
                    return;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mOutStream.write(buffer);
                Log.m2358w("Write to Feature Phone SPP" + buffer.length, new Object[0]);
                BluetoothConnection.this.mMessageHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.m2358w("Exception during write", e);
                Toast.makeText(BTNotificationApplication.getInstance(), "failed to send.", 0).show();
            }
        }

        public void cancel() {
            Log.m2355i(BluetoothConnection.LOG_TAG, "cancel(),  WorkThread is canceled", new Object[0]);
            try {
                this.mSocket.close();
            } catch (IOException e) {
                Log.m2358w("close connected socket failed", e);
            }
        }
    }

    public BluetoothConnection(Handler handler) {
        Log.m2355i(LOG_TAG, "BluetoothConnection(), BluetoothConnection created!", new Object[0]);
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mConnectState = 0;
        this.mMessageHandler = handler;
    }

    public synchronized int getState() {
        Log.m2355i(LOG_TAG, "getState(), mConnectState=" + this.mConnectState, new Object[0]);
        return this.mConnectState;
    }

    private synchronized void setState(int state) {
        Log.m2355i(LOG_TAG, "setState(), state=" + state, new Object[0]);
        this.mConnectState = state;
        this.mMessageHandler.obtainMessage(1, state, -1).sendToTarget();
    }

    public synchronized void startAccept() {
        Log.m2355i(LOG_TAG, "startAccept()", new Object[0]);
        if (this.mClientThread != null) {
            this.mClientThread.cancel();
            this.mClientThread = null;
        }
        if (this.mWorkThread != null) {
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        if (this.mServerThread == null) {
            this.mServerThread = new ServerThread();
            this.mServerThread.start();
        }
        setState(1);
    }

    public synchronized void connectRemoteDevice(BluetoothDevice remoteDevice) {
        Log.m2355i(LOG_TAG, "connectRemoteDevice(), device=" + remoteDevice, new Object[0]);
        if (this.mConnectState == 2 && this.mClientThread != null) {
            this.mClientThread.cancel();
            this.mClientThread = null;
        }
        if (this.mWorkThread != null) {
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        this.mClientThread = new ClientThread(remoteDevice);
        this.mClientThread.start();
        setState(2);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        Log.m2355i(LOG_TAG, "connected(), socket=" + socket + ", device=" + device, new Object[0]);
        if (this.mClientThread != null) {
            this.mClientThread.cancel();
            this.mClientThread = null;
        }
        if (this.mWorkThread != null) {
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        if (this.mServerThread != null) {
            this.mServerThread.cancel();
            this.mServerThread = null;
        }
        this.mWorkThread = new WorkThread(socket);
        this.mWorkThread.start();
        Message msg = this.mMessageHandler.obtainMessage(4);
        Bundle bundle = new Bundle();
        bundle.putString("device_name", device.getName());
        msg.setData(bundle);
        this.mMessageHandler.sendMessage(msg);
        setState(3);
    }

    public synchronized void stop() {
        Log.m2355i(LOG_TAG, "stop()", new Object[0]);
        if (this.mClientThread != null) {
            this.mClientThread.cancel();
            this.mClientThread = null;
        }
        if (this.mWorkThread != null) {
            this.mWorkThread.cancel();
            this.mWorkThread = null;
        }
        if (this.mServerThread != null) {
            this.mServerThread.cancel();
            this.mServerThread = null;
        }
        setState(0);
    }

    public void write(byte[] out) {
        synchronized (this) {
            if (this.mConnectState != 3) {
                return;
            }
            WorkThread r = this.mWorkThread;
            r.write(out);
        }
    }

    private void connectionFailed() {
        Log.m2355i(LOG_TAG, "connectionFailed()", new Object[0]);
        setState(1);
        Message msg = this.mMessageHandler.obtainMessage(5);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothManager.TOAST, "Unable to connect device");
        msg.setData(bundle);
        this.mMessageHandler.sendMessage(msg);
        startAccept();
    }

    private void connectionLost() {
        Log.m2355i(LOG_TAG, "connectionLost()", new Object[0]);
        setState(4);
        startAccept();
        Log.m2355i(LOG_TAG, "connectionLost(), ServerThread restart!", new Object[0]);
    }
}
