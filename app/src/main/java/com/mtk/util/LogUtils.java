package com.mtk.util;

import android.util.Log;

public class LogUtils {
    private static final String TAG = "MZIA";
    public static boolean isDebug = true;

    private LogUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void m2363i(String msg) {
        if (isDebug) {
            Log.i(TAG, msg);
        }
    }

    public static void m2364i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

}
