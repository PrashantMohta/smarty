package com.mtk.remotecamera;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by Dandy on 01-03-2018.
 */

public class MyFaceDetectionListener implements Camera.FaceDetectionListener {
    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces.length > 0){
            Log.d("FaceDetection", "face detected: "+ faces.length +
                    " Face 1 Location X: " + faces[0].rect.centerX() +
                    "Y: " + faces[0].rect.centerY() );
        }
    }
}
