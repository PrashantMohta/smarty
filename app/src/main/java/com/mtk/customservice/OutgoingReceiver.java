package com.mtk.customservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.mtk.data.PreferenceData;
import com.mtk.util.NotifiUtils;
import com.mtk.util.ToastUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by Dandy on 26-02-2018.
 */

public class OutgoingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String pattern = number.toString();
        if(pattern.startsWith("00*")==true)
        {
            setResultData(null); //Block the  call
            String[] command = pattern.split("\\*");
            //ToastUtils.showShortToast(context,"cmd  "+command[0]+","+command[1]);
            if(command[1].equals("001")) // change camera
            {
                int i= PreferenceData.getUseCamera();
                int max =Camera.getNumberOfCameras() -1;
                if(i<max) {
                 i++;
                }else{
                    i=0;
                }
                PreferenceData.setUseCamera(i);
                NotifiUtils.sendNotificationToWatch(context,"Camera Mode Changed","Switch camera to camera "+i,2000);
            } else if(command[1].equals("002"))// use as remote app
            {
                String code;
                ToastUtils.showShortToast(context,"use as remote app");
                PreferenceData.setAppMode(1);
                if(command.length >2){
                if(command[2].equals("0")) {
                    PreferenceData.setAppURL("file:///android_res/raw/test.html"); //Set Default app
                }else if(command[2].equals("1")) {
                    PreferenceData.setAppURL("file:///android_res/raw/test.html"); //Set some other app
                }
                }
                code="current App = " + PreferenceData.getAppURL();
                NotifiUtils.sendNotificationToWatch(context,"Remote app mode",code,2000);
            }else if(command[1].equals("003"))// use as remote camera
            {
                ToastUtils.showShortToast(context,"use as remote camera ");
                PreferenceData.setAppMode(0);
            }
        }
    }


    public Boolean killCall(Context context) {
        try {
            // Get the boring old TelephonyManager
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            // Get the getITelephony() method
            Class classTelephony = Class.forName(telephonyManager.getClass().getName());
            Method methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony");

            // Ignore that the method is supposed to be private
            methodGetITelephony.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = methodGetITelephony.invoke(telephonyManager);

            // Get the endCall method from ITelephony
            Class telephonyInterfaceClass =
                    Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);

        } catch (Exception ex) { // Many things can go wrong with reflection calls
            return false;
        }
        return true;
    }

}
