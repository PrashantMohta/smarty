package com.mtk.remotecamera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mtk.BTNotificationApplication;
import com.mtk.data.PreferenceData;
import com.mtk.service.MainService;

public class RemoteCamera extends Activity {
    static int ratation = 0;
    private final String TAG = "REMOTECAMERAService";
    @SuppressLint({"HandlerLeak"})
    private final Handler handler = new C05691();
    private boolean isNotifiedToCloseByBTDialer = false;
    private Camera mCamera;
    private Preview mPreview;
    private final BroadcastReceiver mReceiver = new C05702();
    private final SubSensorListener mSubSensorListener = new SubSensorListener(this.handler);
    private int numberOfCameras;
    private final MainService service = MainService.getInstance();

    class C05691 extends Handler {
        C05691() {
        }

        public void handleMessage(Message msg) {
            if (msg.what == 123) {
                int orientation = msg.arg1;
                if (orientation > 45 && orientation < 135) {
                    RemoteCamera.ratation = 270;
                } else if (orientation > 135 && orientation < 225) {
                    RemoteCamera.ratation = 180;
                } else if (orientation > 225 && orientation < 315) {
                    RemoteCamera.ratation = 90;
                } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                    RemoteCamera.ratation = 0;
                }
            }
        }
    }

    class C05702 extends BroadcastReceiver {
        C05702() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION.equals(action)) {
                RemoteCamera.this.isNotifiedToCloseByBTDialer = true;
                RemoteCameraService.isIntheProgressOfExit = true;
                RemoteCamera.this.finish();
            } else if (RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION.equals(action) && RemoteCamera.this.mPreview != null) {
                RemoteCamera.this.mPreview.takePicture(RemoteCamera.ratation);
            }
        }
    }

    class SubSensorListener implements SensorEventListener {
        public static final int ORIENTATION_UNKNOWN = -1;
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;
        private final Handler handler;

        public SubSensorListener(Handler handler) {
            this.handler = handler;
        }

        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = -1;
            float X = -values[0];
            float Y = -values[1];
            float Z = -values[2];
            if (4.0f * ((X * X) + (Y * Y)) >= Z * Z) {
                orientation = 90 - Math.round(((float) Math.atan2((double) (-Y), (double) X)) * 57.29578f);
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }
            if (this.handler != null) {
                this.handler.obtainMessage(123, orientation, 0).sendToTarget();
            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        Log.i("REMOTECAMERAService", "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(128);
        IntentFilter filter = new IntentFilter(RemoteCameraService.BT_REMOTECAMERA_EXIT_ACTION);
        filter.addAction(RemoteCameraService.BT_REMOTECAMERA_CAPTURE_ACTION);
        registerReceiver(this.mReceiver, filter);
        @SuppressLint("WrongConstant") SensorManager sm = (SensorManager) BTNotificationApplication.getInstance().getApplicationContext().getSystemService("sensor");
        sm.registerListener(this.mSubSensorListener, sm.getDefaultSensor(1), 2, this.handler);
        requestWindowFeature(1);
        getWindow().addFlags(1024);

        this.mPreview = new Preview(this);
        setContentView(this.mPreview);
        this.numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        //int i = 0;
        //while (i > this.numberOfCameras) {
        ;
        Camera.getCameraInfo(PreferenceData.getUseCamera(), cameraInfo);
        //    i = cameraInfo.facing == 0 ? i + 1 : i + 1;
        //}
        this.mCamera = Camera.open(PreferenceData.getUseCamera());
        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
        this.mPreview.setCamera(this.mCamera);

        this.service.sendCAPCResult(String.valueOf(1) + " 0 ");
        RemoteCameraService.isLaunched = true;
        RemoteCameraService.isIntheProgressOfExit = false;

    }

    protected void onResume() {
        Log.i("REMOTECAMERAService", "onResume");
        super.onResume();
        try {
            this.mCamera = Camera.open(PreferenceData.getUseCamera());
            if (this.mCamera == null) {
                Log.i("REMOTECAMERAService", "Can't open the camera");
                this.isNotifiedToCloseByBTDialer = false;
                finish();
            }
        } catch (Exception e) {
            Log.i("REMOTECAMERAService", "onResume and isNotifiedToCloseByBTDialer = true");
            this.isNotifiedToCloseByBTDialer = false;
            finish();
        }
        this.mPreview.setCamera(this.mCamera);
        this.service.sendCAPCResult(String.valueOf(1) + " 0 ");
        RemoteCameraService.isLaunched = true;
        RemoteCameraService.isIntheProgressOfExit = false;
    }

    @SuppressLint("WrongConstant")
    public void finish() {
        Log.i("REMOTECAMERAService", "finish");
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mPreview.setCamera(null);
            this.mCamera.release();
            this.mCamera = null;
        }
        ((SensorManager) BTNotificationApplication.getInstance().getApplicationContext().getSystemService("sensor")).unregisterListener(this.mSubSensorListener);
        super.finish();
    }

    protected void onPause() {
        super.onPause();
        finish();
        Log.i("REMOTECAMERAService", "onPause");
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mPreview.setCamera(null);
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i("REMOTECAMERAService", "onDestroy: isNotifiedToCloseByBTDialer is:" + this.isNotifiedToCloseByBTDialer);
        unregisterReceiver(this.mReceiver);
        if (this.isNotifiedToCloseByBTDialer) {
            this.isNotifiedToCloseByBTDialer = false;
        } else {
            this.service.sendCAPCResult(String.valueOf(3) + " 0 ");
        }
        RemoteCameraService.isLaunched = false;
        RemoteCameraService.isIntheProgressOfExit = false;
    }
}
