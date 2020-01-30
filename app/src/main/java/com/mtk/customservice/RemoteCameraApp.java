package com.mtk.customservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mtk.BTNotificationApplication;
import com.mtk.data.PreferenceData;
import com.mtk.remotecamera.MyFaceDetectionListener;
import com.mtk.remotecamera.Preview;
import com.mtk.remotecamera.RemoteCamera;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.ToastUtils;

/**
 * Created by Dandy on 05-03-2018.
 */

public class RemoteCameraApp extends Service {
    private final String TAG = "REMOTECAMERAService";
    private boolean isNotifiedToCloseByBTDialer = false;
    private AppRunnerPreview mPreview;
    private final BroadcastReceiver mReceiver = new RemoteCameraApp.C05702();
    private final MainService service = MainService.getInstance();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    class C05702 extends BroadcastReceiver {
        C05702() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION.equals(action)) {
                RemoteCameraApp.this.isNotifiedToCloseByBTDialer = true;
                RemoteCameraService.isIntheProgressOfExit = true;

                RemoteCameraApp.this.mPreview.appActionMinimize();
                RemoteCameraApp.this.finish();
            } else if (RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION.equals(action) && RemoteCameraApp.this.mPreview != null) {
                //perform app action
                RemoteCameraApp.this.mPreview.appAction();
                RemoteCameraApp.this.finish();

            }
        }
    }
    @SuppressLint("WrongConstant")
    public void finish() {
        Log.i("REMOTECAMERAService", "onDestroy: isNotifiedToCloseByBTDialer is:" + this.isNotifiedToCloseByBTDialer);
        //unregisterReceiver(this.mReceiver);
        if (this.isNotifiedToCloseByBTDialer) {
            this.isNotifiedToCloseByBTDialer = false;
        } else {
            this.service.sendCAPCResult(String.valueOf(3) + " 0 ");
        }
        RemoteCameraService.isLaunched = false;
        RemoteCameraService.isIntheProgressOfExit = false;
        RemoteCameraApp.this.stopSelf();

    }

    public void onCreate() {
        ToastUtils.showShortToast(getApplicationContext(),"starting app mode");
        Log.i("REMOTECAMERAService", "onCreate");
        IntentFilter filter = new IntentFilter(RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION);
        filter.addAction(RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION);
        registerReceiver(this.mReceiver, filter);
        this.mPreview = new AppRunnerPreview(this);
        this.service.sendCAPCResult(String.valueOf(1) + " 0 ");
        RemoteCameraService.isLaunched = true;
        RemoteCameraService.isIntheProgressOfExit = false;

    }
}
