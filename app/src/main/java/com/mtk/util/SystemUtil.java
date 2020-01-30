package com.mtk.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Images.Media;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.mtk.map.BMessage;
import java.util.List;

public class SystemUtil {
    public static final int NETTYPE_CMNET = 3;
    public static final int NETTYPE_CMWAP = 2;
    public static final int NETTYPE_WIFI = 1;


    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean hasNet(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    public static String getUrlPath(Activity context, Uri uri) {
        if (uri.toString().contains("file:/")) {
            return uri.getPath();
        }
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("_data"));
    }

    @SuppressLint({"NewApi"})
    public static String getPath(Activity context, Uri contentUri) {
        String filePath = "";
        Cursor cursor;
        if (VERSION.SDK_INT >= 19) {
            String id = DocumentsContract.getDocumentId(contentUri).split(BMessage.SEPRATOR)[1];
            String[] column = new String[]{"_data"};
            cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, column, "_id=?", new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
            cursor.close();
            return null;
        } else if (contentUri.toString().contains("file:/")) {
            return contentUri.getPath();
        } else {
            cursor = context.getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    public String getRealPathFromURI(Activity context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndexOrThrow("_data"));
    }

    public static int getScreenWidth(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity context) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getSystemVersion() {
        return VERSION.SDK_INT;
    }

    public static int getNetworkType(Context context) {
        int netType = 0;
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (networkInfo == null) {
            return 0;
        }
        int nType = networkInfo.getType();
        if (nType == 0) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                netType = extraInfo.toLowerCase().equals("cmnet") ? 3 : 2;
            }
        } else if (nType == 1) {
            netType = 1;
        }
        return netType;
    }

    public static void hideSoftKeyborad(Activity context) {
        View v = context.getWindow().peekDecorView();
        if (v != null && v.getWindowToken() != null) {
            ((InputMethodManager) context.getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }



    @SuppressLint({"NewApi", "NewApi"})
    public static void copyText(Context context, String text) {
        if (VERSION.SDK_INT >= 11) {
            ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", text));
        } else {
            ((android.text.ClipboardManager) context.getSystemService("clipboard")).setText(text);
        }
    }

    public static String getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService("activity");
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static long getmem_UNUSED(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService("activity");
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    @SuppressLint({"NewApi"})
    public static long getmem_used(Context mContext) {
        ActivityManager am = (ActivityManager) mContext.getSystemService("activity");
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        return (mi.totalMem - mi.availMem) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        List<RunningServiceInfo> serviceList = ((ActivityManager) mContext.getSystemService("activity")).getRunningServices(30);
        if (serviceList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (((RunningServiceInfo) serviceList.get(i)).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
