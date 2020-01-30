package com.mtk.remotecamera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mtk.customservice.RemoteCameraApp;
import com.mtk.data.Log;
import com.mtk.data.MessageObj;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.mtk.service.MainService;
import com.mtk.util.SystemUtil;
import com.mtk.util.ToastUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class RemoteCameraService extends BroadcastReceiver {
    public static final String BT_REMOTECAMERA_BROADCAST_ACTION = "com.mtk.RemoteCamera";
    public static final String BT_REMOTECAMERA_CAPTURE_ACTION = "com.mtk.RemoteCamera.CAPTURE";
    public static final String BT_REMOTECAMERA_EXIT_ACTION = "com.mtk.RemoteCamera.EXIT";
    public static final String BT_REMOTEOPENRING_BROADCAST_ACTION = "com.mtk.RemoteOpenRing";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    private static final String TAG = "REMOTECAMERAService";
    public static boolean isIntheProgressOfExit = false;
    public static boolean isLaunched = false;
    public static boolean needPreview = false;
    private String mRemoteCameraCommand = null;

    public static class Commands {
        public static final int CLOSE_PHONE_RING = 6;
        public static final String NUM_OF_CAP_ACTIFITY_ARGS = " 1 ";
        public static final String NUM_OF_EXIT_ACTIFITY_ARGS = " 0 ";
        public static final String NUM_OF_START_ACTIFITY_ARGS = " 0 ";
        public static final int OPEN_PHONE_RING = 5;
        public static final int POSITION_OF_CAP = 2;
        public static final int POSITION_OF_COMMAND = 0;
        public static final int POSITION_OF_EXIT_ACTIVITY = 3;
        public static final int POSITION_OF_PREVIEW = 4;
        public static final int POSITION_OF_START_ACTIVITY = 1;
    }

    public RemoteCameraService() {
        Log.m2355i(TAG, "RemoteCameraService(), RemoteCameraService created!", new Object[0]);
    }

    public void onReceive(Context context, Intent intent) {
        needPreview = false;
        String action = intent.getAction();
        MainService service = MainService.getInstance();
        if (BT_REMOTECAMERA_BROADCAST_ACTION.equals(action)) {
            try {
                this.mRemoteCameraCommand = new String(intent.getByteArrayExtra("EXTRA_DATA"), MessageObj.CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String[] commands = this.mRemoteCameraCommand.split(" ");
            Log.m2355i(TAG, "RemoteCameraService onReceive(), commands :" + Arrays.toString(commands), new Object[0]);
            switch (Integer.valueOf(commands[0]).intValue()) {
                case 1:
                    Log.m2355i(TAG, "isLaunched: " + isLaunched, new Object[0]);
                    Log.m2355i(TAG, "isIntheProgressOfExit: " + isIntheProgressOfExit, new Object[0]);
                    if (Util.isScreenLocked(context)) {
                        if(PreferenceData.getAppMode()==0){
                        service.sendCAPCResult(String.valueOf(-1) + " 0 ");
                        return;}
                        else{
                            // start in service mode
                            ToastUtils.showShortToast(context,"trying app mode");
                            Intent launchIntent=new Intent(context, RemoteCameraApp.class);
                            launchIntent.setFlags(268435456);
                            context.startService(launchIntent);
                        }
                    } else if (!Util.isScreenOn(context)) {
                        if(PreferenceData.getAppMode()==0){
                        service.sendCAPCResult(String.valueOf(-1) + " 0 ");
                        return;}
                    else{
                        // start in service mode
                        ToastUtils.showShortToast(context,"trying app mode");
                        Intent launchIntent=new Intent(context, RemoteCameraApp.class);
                        launchIntent.setFlags(268435456);
                        context.startService(launchIntent);
                    }
                    } else if (isLaunched && !isIntheProgressOfExit) {
                        service.sendCAPCResult(String.valueOf(1) + " 0 ");
                        return;
                    } else if (isIntheProgressOfExit) {
                        service.sendCAPCResult(String.valueOf(-1) + " 0 ");
                        return;
                    } else {
                        if(PreferenceData.getAppMode()==0){
                        Intent launchIntent = new Intent();
                        launchIntent.setFlags(268435456);
                        launchIntent.setClass(context, RemoteCamera.class);
                        context.startActivity(launchIntent);}
                        else{
                            // start in service mode
                            ToastUtils.showShortToast(context,"trying app mode");
                            Intent launchIntent=new Intent(context, RemoteCameraApp.class);
                            launchIntent.setFlags(268435456);
                            context.startService(launchIntent);
                        }
                        return;
                    }
                case 2:
                    Intent broadcastCaptureIntent = new Intent();
                    broadcastCaptureIntent.setAction(BT_REMOTECAMERA_CAPTURE_ACTION);
                    context.sendBroadcast(broadcastCaptureIntent);
                    return;
                case 3:
                    if (isLaunched) {
                        isIntheProgressOfExit = true;
                    }
                    Intent broadcastExitIntent = new Intent();
                    broadcastExitIntent.setAction(BT_REMOTECAMERA_EXIT_ACTION);
                    context.sendBroadcast(broadcastExitIntent);
                    return;
                case 4:
                    Log.m2355i(TAG, "needPreview = true", new Object[0]);
                    needPreview = true;
                    return;
                case 5:
                    service.sendCAPCResult(String.valueOf(5) + " 0 ");
                    if (Boolean.valueOf(SystemUtil.isServiceRunning(MainService.sContext, "com.mtk.service.RingReciver")).booleanValue()) {
                        MainService.getInstance().stopRingService();
                        MainService.getInstance().startRingService();
                        return;
                    }
                    Log.m2355i(TAG, "service is stop", new Object[0]);
                    MainService.getInstance().startRingService();
                    return;
                case 6:
                    Log.m2353e(TAG, "return6", new Object[0]);
                    MainService.getInstance().stopRingService();
                    return;
                default:
                    return;
            }
        }
    }
}
