package com.mtk.data;

import com.mtk.btconnection.BluetoothConnection;

public class Log {
    private static final int LEVEL = 2;

    public static void m2351d(String tag, String msgFormat, Object... args) {
        try {
            android.util.Log.d(tag, String.format(msgFormat, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2352d(String tag, Throwable t, String msgFormat, Object... args) {
        try {
            android.util.Log.d(tag, String.format(msgFormat, args), t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2355i(String tag, String msgFormat, Object... args) {
        try {
            android.util.Log.i(tag, String.format(msgFormat, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2356i(String tag, Throwable t, String msgFormat, Object... args) {
        try {
            android.util.Log.i(tag, String.format(msgFormat, args), t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2358w(String msgFormat, Object... args) {
        try {
            android.util.Log.w(BluetoothConnection.LOG_TAG, String.format(msgFormat, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2357w(String tag, Throwable t, String msgFormat, Object... args) {
        try {
            android.util.Log.w(tag, String.format(msgFormat, args), t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2353e(String tag, String msgFormat, Object... args) {
        try {
            android.util.Log.e(tag, String.format(msgFormat, args));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void m2354e(String tag, Throwable t, String msgFormat, Object... args) {
        try {
            android.util.Log.e(tag, String.format(msgFormat, args), t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
