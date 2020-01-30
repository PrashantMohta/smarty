package com.mtk.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static boolean isShow = true;

    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showShortToast(Context context, CharSequence message) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), message, 0).show();
        }
    }

    public static void showShortToast(Context context, int msgResId) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), msgResId, 0).show();
        }
    }

    public static void showLongToast(Context context, CharSequence message) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), message, 1).show();
        }
    }

    public static void showLongToast(Context context, int msgResId) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), msgResId, 1).show();
        }
    }

    public static void showToast(Context context, CharSequence message, int duration) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), message, duration).show();
        }
    }

    public static void showToast(Context context, int msgResId, int duration) {
        if (isShow) {
            Toast.makeText(context.getApplicationContext(), msgResId, duration).show();
        }
    }
}
