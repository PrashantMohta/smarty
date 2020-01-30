package com.mtk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.mtk.BTNotificationApplication;

public final class PreferenceData {
    private static final String LOG_TAG = "PreferenceData";
    public static final String PREFERENCE_KEY_ACCESSIBILITY = "show_accessibility_menu_preference";
    public static final String PREFERENCE_KEY_ALWAYS_FORWARD = "always_forward_preference";
    public static final String PREFERENCE_KEY_APP_INFO = "app_info";
    public static final String PREFERENCE_KEY_CALL = "enable_call_service_preference";
    public static final String PREFERENCE_KEY_CURRENT_VERSION = "current_version_preference";
    public static final String PREFERENCE_KEY_NOTIFI = "enable_notifi_service_preference";
    public static final String PREFERENCE_KEY_SELECT_BLOCKS = "select_blocks_preference";
    public static final String PREFERENCE_KEY_SELECT_NOTIFICATIONS = "select_notifi_preference";
    public static final String PREFERENCE_KEY_SHOW_CONNECTION_STATUS = "show_connection_status_preference";
    public static final String PREFERENCE_KEY_SMS = "enable_sms_service_preference";
    private static final Context sContext = BTNotificationApplication.getInstance().getApplicationContext();
    private static final SharedPreferences sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(sContext);

    public  static void setUseCamera(int i)
        {
            sSharedPreferences.edit().putInt("useCamera", i).commit();
        }

    public static int getUseCamera() {
        int useCamera = sSharedPreferences.getInt("useCamera", 0);
        return useCamera;
    }


    public  static void setAppMode(int i)
    {
        sSharedPreferences.edit().putInt("CameraAppMode", i).commit();
    }

    public static int getAppMode() {
        int AppMode = sSharedPreferences.getInt("CameraAppMode", 0);
        return AppMode;
    }
    public  static void setAppURL(String i)
    {
        sSharedPreferences.edit().putString("CameraAppURL", i).commit();
    }

    public static String getAppURL() {
        String AppURL = sSharedPreferences.getString("CameraAppURL","file:///android_res/raw/test.html");
        return AppURL;
    }

    public static boolean isNotificationPrivate() {
        boolean isEnable = sSharedPreferences.getBoolean("NotificationPrivate", true);
        Log.m2355i(LOG_TAG, "isNotificationPrivate(), isEnable=" + isEnable, new Object[0]);
        return isEnable;
    }

    public static void setNotificationPrivate(boolean flag) {
        sSharedPreferences.edit().putBoolean("NotificationPrivate", flag).commit();
    }

    public static boolean isSmsServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_SMS, true);
        Log.m2355i(LOG_TAG, "isSmsServiceEnable(), isEnable=" + isEnable, new Object[0]);
        return isEnable;
    }

    public static void setSmsServiceEnable(boolean flag) {
        sSharedPreferences.edit().putBoolean(PREFERENCE_KEY_SMS, flag).commit();
    }

    public static boolean isNotificationServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_NOTIFI, true);
        Log.m2355i(LOG_TAG, "isNotifiServiceEnable(), isEnable=" + isEnable, new Object[0]);
        return isEnable;
    }

    public static void setNotificationServiceEnable(boolean flag) {
        sSharedPreferences.edit().putBoolean(PREFERENCE_KEY_NOTIFI, flag).commit();
    }

    public static boolean isCallServiceEnable() {
        boolean isEnable = sSharedPreferences.getBoolean(PREFERENCE_KEY_CALL, true);
        Log.m2355i(LOG_TAG, "isCallServiceEnable(), isEnable=" + isEnable, new Object[0]);
        return isEnable;
    }

    public static void setCallServiceEnable(boolean flag) {
        sSharedPreferences.edit().putBoolean(PREFERENCE_KEY_CALL, flag).commit();
    }

    public static boolean isShowConnectionStatus() {
        boolean isShow = sSharedPreferences.getBoolean(PREFERENCE_KEY_SHOW_CONNECTION_STATUS, true);
        Log.m2355i(LOG_TAG, "isShowConnectionStatus(), isShow=" + isShow, new Object[0]);
        return isShow;
    }

    public static void setShowConnectionStatus(boolean flag) {
        sSharedPreferences.edit().putBoolean(PREFERENCE_KEY_SHOW_CONNECTION_STATUS, flag).commit();
    }

    public static boolean isAlwaysForward() {
        boolean isAlways = sSharedPreferences.getBoolean(PREFERENCE_KEY_ALWAYS_FORWARD, true);
        Log.m2355i(LOG_TAG, "isAlwaysForward(), isAlways=" + isAlways, new Object[0]);
        return isAlways;
    }

    public static void setAlwaysForward(boolean flag) {
        sSharedPreferences.edit().putBoolean(PREFERENCE_KEY_ALWAYS_FORWARD, flag).commit();
    }

    public static boolean isNeedPush() {
        boolean needPush;
        if (isAlwaysForward() || Util.isScreenLocked(sContext)) {
            needPush = true;
        } else {
            needPush = false;
        }
        Log.m2355i(LOG_TAG, "isNeedForward(), needPush=" + needPush, new Object[0]);
        return needPush;
    }


}
