package com.mtk.customservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.mtk.remotecamera.RemoteCamera;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dandy on 05-03-2018.
 */


/* compiled from: RemoteCamera */
public class AppRunnerPreview {
    private final String TAG = "REMOTECAMERAappService";
    private Camera mCamera;
    private final Context mContext;
    private long mCurrentTime;

    private Camera.Size mPictureSize;
    private Camera.Size mAppRunnerPreviewSize;
    private List<Camera.Size> mSupportedPictureSizes;
    private List<Camera.Size> mSupportedAppRunnerPreviewSizes;
    private int ratation;
    private WebView webview;
    byte[] mPreviewJpegData;
    Handler handler = null;
    private boolean applicationmode=true; //camera by default
    private Bitmap bp;
    /* compiled from: RemoteCamera */

    public synchronized void updateAppCanvas(){
        if (RemoteCameraService.needPreview) {
            if (AppRunnerPreview.this.mCurrentTime == 0) {
                AppRunnerPreview.this.mCurrentTime = System.currentTimeMillis();
            }
            Bitmap bitmap=Bitmap.createBitmap(240,240,Bitmap.Config.ARGB_4444);
            if (System.currentTimeMillis() - AppRunnerPreview.this.mCurrentTime > 333) {
                if (!AppRunnerPreview.this.applicationmode) // add condition to choose application mode
                {
                    bitmap = webview.getDrawingCache();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos); // originally 80
                AppRunnerPreview.this.mPreviewJpegData = baos.toByteArray();
                String cmdOfList = String.valueOf(4) + RemoteCameraService.Commands.NUM_OF_CAP_ACTIFITY_ARGS + String.valueOf(AppRunnerPreview.this.mPreviewJpegData.length) + " ";
                MainService service = MainService.getInstance();
                service.sendCAPCResult(cmdOfList);
                service.sendCAPCData(AppRunnerPreview.this.mPreviewJpegData);
                Log.i("CameraAppRunnerPreview", "vedio data has sent ...");
            } else {
                Log.i("CameraAppRunnerPreview", "vedio data did not need to send ...");
            }
        }
    }


        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("REMOTECAMERAService", "onPictureTaken");
            if (!"mounted".equals(Environment.getExternalStorageState()) || Util.getAvailableStore(Environment.getExternalStorageDirectory().getPath()) < 2000) {
                sendCaptureFail();
            } else {
                sendCaptureData(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();

            }
            Log.i("Remote Capture", "Capture success");
        }

        private void sendCaptureFail() {
            MainService.getInstance().sendCAPCResult(String.valueOf(-1) + " 0 ");
        }
        // the final finished image data TO-DO
        private Bitmap sendCaptureData(byte[] data) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            return bitmap;
        }

    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, true);
        }
    }
    public AppRunnerPreview(Context context) {
        ToastUtils.showShortToast(context,"starting AppRunnerPreview ");

        this.mContext = context;
        this.applicationmode=false;//(PreferenceData.getAppMode()==0);
        this.mCurrentTime = 0;
        if(!this.applicationmode){
            this.webview = new WebView(mContext);
            webview.setDrawingCacheEnabled(true);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.getSettings().setGeolocationEnabled(true);
            webview.setWebChromeClient(new GeoWebChromeClient());
            webview.getSettings().setGeolocationDatabasePath( context.getFilesDir().getPath() );

            webview.measure(240, 240);
            webview.layout(0, 0, 720, 720);

            webview.loadUrl(PreferenceData.getAppURL());
            webview.setWebViewClient(new WebViewClient());

            handler= new Handler();
            final int delay = 1000; //milliseconds
            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    updateAppCanvas();
                    handler.postDelayed(this, delay);
                }
            }, delay);
        }

    }

    public void appActionMinimize() {
        sendCaptureFail();
        handler.removeCallbacksAndMessages(null);

    }
    public void appAction() {
        sendCaptureFail();
        handler.removeCallbacksAndMessages(null);

    }
}
