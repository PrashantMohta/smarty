package com.mtk.remotecamera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.dandy.smartwatch.modded.R;
import com.mtk.data.PreferenceData;
import com.mtk.data.Util;
import com.mtk.remotecamera.RemoteCameraService.Commands;
import com.mtk.service.MainService;
import com.mtk.util.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/* compiled from: RemoteCamera */
public class Preview extends ViewGroup implements Callback {
    private final String TAG = "REMOTECAMERAService";
    Activity mAcitivity;
    private final AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    private Camera mCamera;
    byte[] mCaptureJpegData;
    private final Context mContext;
    private long mCurrentTime;
    private final SurfaceHolder mHolder;
    private Size mPictureSize;
    byte[] mPreviewJpegData;
    private Size mPreviewSize;
    private List<Size> mSupportedPictureSizes;
    private List<Size> mSupportedPreviewSizes;
    private final SurfaceView mSurfaceView;
    private final TakePictureCallback mTakePictureCallback = new TakePictureCallback();
    private int ratation;
    private WebView webview;
    private boolean applicationmode=true; //camera by default
 private  Bitmap bp;
    /* compiled from: RemoteCamera */
    class C05681 implements PreviewCallback {

        C05681() {
        }

        public synchronized void onPreviewFrame(byte[] data, Camera camera) {
            if (RemoteCameraService.needPreview) {
                if (Preview.this.mCurrentTime == 0) {
                    Preview.this.mCurrentTime = System.currentTimeMillis();
                }
                Bitmap bitmap;
                if (System.currentTimeMillis() - Preview.this.mCurrentTime > 333) {
                    if(Preview.this.applicationmode) // add condition to choose application mode
                    {
                    double scaleRation;
                    Log.i("CameraPreview", "vedio data come ...");
                    Preview.this.mCurrentTime = System.currentTimeMillis();
                    Parameters parameters = camera.getParameters();
                    int imageFormat = parameters.getPreviewFormat();
                    int previewWidth = parameters.getPreviewSize().width;
                    int previewHight = parameters.getPreviewSize().height;
                    Rect rect = new Rect(0, 0, previewWidth, previewHight);
                    YuvImage yuvImg = new YuvImage(data, imageFormat, previewWidth, previewHight, null);
                    ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
                    yuvImg.compressToJpeg(rect, 70, outputstream);
                    bitmap = BitmapFactory.decodeByteArray(outputstream.toByteArray(), 0, outputstream.size());
                    if (previewWidth > previewHight) {
                        scaleRation = (double) (previewWidth / 170);
                    } else {
                        scaleRation = (double) (previewHight / 170);
                    }
                    Matrix matrix = new Matrix();
                    Preview.this.ratation = RemoteCamera.ratation;
                    if (Preview.this.ratation == 0 || Preview.this.ratation == 180) {
                        matrix.postRotate((float) (Preview.this.ratation + 90));
                    }
                    if (Preview.this.ratation == 90 || Preview.this.ratation == 270) {
                        matrix.postRotate((float) (Preview.this.ratation - 90));
                    }
                    if (PreferenceData.getUseCamera() == 1) {
                        matrix.postRotate((float) (Preview.this.ratation - 180));
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (((double) previewWidth) / scaleRation), (int) (((double) previewHight) / scaleRation), false);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    }else{

                        //bitmap = Bitmap.createBitmap(240,240,Bitmap.Config.ARGB_4444);
                        //Bitmap img=drawText("M="+(Calendar.getInstance().get(Calendar.MILLISECOND)), 240,240, Color.BLACK);
                        bitmap=webview.getDrawingCache();

                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.JPEG, 50, baos); // originally 80
                    Preview.this.mPreviewJpegData = baos.toByteArray();
                    String cmdOfList = String.valueOf(4) + Commands.NUM_OF_CAP_ACTIFITY_ARGS + String.valueOf(Preview.this.mPreviewJpegData.length) + " ";
                    MainService service = MainService.getInstance();
                    service.sendCAPCResult(cmdOfList);
                    service.sendCAPCData(Preview.this.mPreviewJpegData);
                    Log.i("CameraPreview", "vedio data has sent ...");
                } else {
                    Log.i("CameraPreview", "vedio data did not need to send ...");
                }
            }
        }
    }
    public synchronized void updateAppCanvas(){
    if (RemoteCameraService.needPreview) {
        if (Preview.this.mCurrentTime == 0) {
            Preview.this.mCurrentTime = System.currentTimeMillis();
        }
        Bitmap bitmap=Bitmap.createBitmap(240,240,Bitmap.Config.ARGB_4444);
        if (System.currentTimeMillis() - Preview.this.mCurrentTime > 333) {
            if (!Preview.this.applicationmode) // add condition to choose application mode
            {
                bitmap = webview.getDrawingCache();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 50, baos); // originally 80
            Preview.this.mPreviewJpegData = baos.toByteArray();
            String cmdOfList = String.valueOf(4) + Commands.NUM_OF_CAP_ACTIFITY_ARGS + String.valueOf(Preview.this.mPreviewJpegData.length) + " ";
            MainService service = MainService.getInstance();
            service.sendCAPCResult(cmdOfList);
            service.sendCAPCData(Preview.this.mPreviewJpegData);
            Log.i("CameraPreview", "vedio data has sent ...");
        } else {
            Log.i("CameraPreview", "vedio data did not need to send ...");
        }
    }
}

    public Bitmap drawText(String text, int textWidth,int textHeight, int color) {

        // Get text dimensions
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#ff00ff"));
        textPaint.setTextSize(30);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

        // Create bitmap and canvas to draw to
        Bitmap b = Bitmap.createBitmap(textWidth,textHeight, Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);

        // Draw background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawPaint(paint);

        // Draw text
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }

    /* compiled from: RemoteCamera */
    private final class AutoFocusCallback implements android.hardware.Camera.AutoFocusCallback {
        private AutoFocusCallback() {
        }

        public void onAutoFocus(boolean success, Camera camera) {
            Log.i("REMOTECAMERAService", "onAutoFocus Callback");
            camera.cancelAutoFocus();
            camera.takePicture(null, null, Preview.this.mTakePictureCallback);
        }
    }

    /* compiled from: RemoteCamera */
    private final class TakePictureCallback implements PictureCallback {
        private TakePictureCallback() {
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i("REMOTECAMERAService", "onPictureTaken");
            if (!"mounted".equals(Environment.getExternalStorageState()) || Util.getAvailableStore(Environment.getExternalStorageDirectory().getPath()) < 2000) {
                sendCaptureFail();
            } else {
                sendCaptureData(data);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                if (Preview.this.ratation == 0 || Preview.this.ratation == 180) {
                    matrix.postRotate((float) (Preview.this.ratation + 90));
                }
                if (Preview.this.ratation == 90 || Preview.this.ratation == 270) {
                    matrix.postRotate((float) (Preview.this.ratation - 90));
                }

                File pictureFile = Preview.this.getOutputMediaFile(1);
                if (pictureFile == null) {
                    Log.d("REMOTECAMERAService", "Error creating media file, check storage permissions: ");
                    return;
                }
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Preview.this.mContext.sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.parse("file://" + pictureFile.getAbsolutePath())));
                } catch (FileNotFoundException e) {
                } catch (IOException e2) {
                }
            }
            Log.i("Remote Capture", "Capture success");
            camera.setDisplayOrientation(0);
            camera.startPreview();
            startFaceDetection();
        }

        private void sendCaptureFail() {
            MainService.getInstance().sendCAPCResult(String.valueOf(-1) + " 0 ");
        }
// the final finished image data TO-DO
        private Bitmap sendCaptureData(byte[] data) {
            double scaleRation;
            MainService service = MainService.getInstance();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            int thumbnailWidth = bitmap.getWidth();
            int thumbnailHeight = bitmap.getHeight();
            if (thumbnailWidth > thumbnailHeight) {
                scaleRation = (double) (thumbnailWidth / 170);
            } else {
                scaleRation = (double) (thumbnailHeight / 170);
            }
            Matrix matrix = new Matrix();
            if (Preview.this.ratation == 0 || Preview.this.ratation == 180) {
                matrix.postRotate((float) (Preview.this.ratation + 90));
            }
            if (Preview.this.ratation == 90 || Preview.this.ratation == 270) {
                matrix.postRotate((float) (Preview.this.ratation - 90));
            }
            if(PreferenceData.getUseCamera()==1)
            {
                matrix.postRotate((float) (Preview.this.ratation - 180));
            }
            bitmap = Bitmap.createBitmap(Bitmap.createScaledBitmap(bitmap, (int) (((double) thumbnailWidth) / scaleRation), (int) (((double) thumbnailHeight) / scaleRation), false), 0, 0, (int) (((double) thumbnailWidth) / scaleRation), (int) (((double) thumbnailHeight) / scaleRation), matrix, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(CompressFormat.JPEG, 90, baos); // try reducing quality original 90
            Preview.this.mCaptureJpegData = baos.toByteArray();
            service.sendCAPCResult(String.valueOf(2) + Commands.NUM_OF_CAP_ACTIFITY_ARGS + String.valueOf(Preview.this.mCaptureJpegData.length) + " ");
            service.sendCAPCData(Preview.this.mCaptureJpegData);
            return bitmap;
        }
    }

    public Preview(Context context) {
        super(context);
        this.mContext = context;
        this.mSurfaceView = new SurfaceView(context);
        addView(this.mSurfaceView);
        this.mCurrentTime = 0;
        this.mHolder = this.mSurfaceView.getHolder();
        this.mHolder.addCallback(this);
        if(PreferenceData.getAppMode()!=0)
        {this.applicationmode=false;}
        else
        {this.applicationmode=true;}
        ToastUtils.showShortToast(context,this.applicationmode+" use the camera mode");
        if(!this.applicationmode){

            this.webview = new WebView(mContext);
            webview.setDrawingCacheEnabled(true);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.measure(240, 240);
            webview.layout(0, 0, 720, 720);
            webview.loadUrl("file:///android_res/raw/test.html");
            webview.setWebViewClient(new WebViewClient());
            final Handler handler = new Handler();
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

    public void setCamera(Camera camera) {
        this.mCamera = camera;
        if (this.mCamera != null) {
            this.mSupportedPreviewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
            this.mSupportedPictureSizes = this.mCamera.getParameters().getSupportedPictureSizes();
            requestLayout();
        }
    }

    public void takePicture(int rote) {
        if (this.mCamera != null) {
            this.ratation = rote;
            this.mCamera.autoFocus(this.mAutoFocusCallback);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        if (this.mSupportedPreviewSizes != null) {
            this.mPreviewSize = getOptimalPreviewSize(this.mSupportedPreviewSizes, width, height);
        }
        if (this.mSupportedPictureSizes != null) {
            this.mPictureSize = getOptimalPreviewSize(this.mSupportedPictureSizes, width, height);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            View child = getChildAt(0);
            int width = r - l;
            int height = b - t;
            int previewWidth = width;
            int previewHeight = height;
            if (this.mPreviewSize != null) {
                previewWidth = this.mPreviewSize.width;
                previewHeight = this.mPreviewSize.height;
            }
            if (width * previewHeight > height * previewWidth) {
                int scaledChildWidth = (previewWidth * height) / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
                return;
            }
            int scaledChildHeight = (previewHeight * width) / previewWidth;
            child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
        }
    }
    public void startFaceDetection(){
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();

        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0){
            // camera supports face detection, so can start it:
            mCamera.startFaceDetection();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("REMOTECAMERAService", "surfaceCreated");
        if(this.applicationmode){
        try {
            if (this.mCamera != null) {
                this.mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e("REMOTECAMERAService", "IOException caused by setPreviewDisplay()", exception);
        }}
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if(this.applicationmode){
        Log.i("REMOTECAMERAService", "surfaceDestroyed");
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback(null);
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
        }
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        double targetRatio = ((double) w) / ((double) h);
        if (sizes == null) {
            return null;
        }
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (int i = sizes.size() - 1; i > 0; i--) {
            Size size = (Size) sizes.get(i);
            if (Math.abs((((double) size.width) / ((double) size.height)) - targetRatio) <= 0.1d && ((double) Math.abs(size.height - targetHeight)) < minDiff) {
                optimalSize = size;
                minDiff = (double) Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize != null) {
            return optimalSize;
        }
        minDiff = Double.MAX_VALUE;
        for (Size size2 : sizes) {
            if (((double) Math.abs(size2.height - targetHeight)) < minDiff) {
                optimalSize = size2;
                minDiff = (double) Math.abs(size2.height - targetHeight);
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if(this.applicationmode){
        if (this.mCamera != null) {

            Log.i("REMOTECAMERAService", "surfaceChanged");
            Parameters parameters = this.mCamera.getParameters();
            parameters.setPreviewSize(this.mPreviewSize.width, this.mPreviewSize.height);
            parameters.setPictureSize(this.mPictureSize.width, this.mPictureSize.height);
            requestLayout();
            this.mCamera.setParameters(parameters);
            this.mCamera.setPreviewCallback(new C05681());
            this.mCamera.startPreview();
            startFaceDetection();
        }}
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Notification");
        if (mediaStorageDir.exists() || mediaStorageDir.mkdirs()) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            if (type == 1) {
                return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
            }
            if (type == 2) {
                return new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
            }
            return null;
        }
        Log.d("MyCameraApp", "failed to create directory");
        return null;
    }
}
