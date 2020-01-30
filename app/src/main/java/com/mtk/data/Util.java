package com.mtk.data;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.internal.view.SupportMenu;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.MotionEventCompat;
import com.mtk.Constants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TimeZone;

public final class Util {
    private static final String LOG_TAG = "Util";
    private static int sMessageId = 256;

    public static int genMessageId() {
        Log.m2355i(LOG_TAG, "genMessageId(), messageId=" + sMessageId, new Object[0]);
        int i = sMessageId;
        sMessageId = i + 1;
        return i;
    }

    public static ApplicationInfo getAppInfo(Context context, CharSequence packageName) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(packageName.toString(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.m2355i(LOG_TAG, "getAppInfo(), appInfo=" + appInfo, new Object[0]);
        return appInfo;
    }

    public static String getAppName(Context context, ApplicationInfo appInfo) {
        String appName;
        if (context == null || appInfo == null) {
            appName = Constants.NULL_TEXT_NAME;
        } else {
            appName = context.getPackageManager().getApplicationLabel(appInfo).toString();
        }
        Log.m2355i(LOG_TAG, "getAppName(), appName=" + appName, new Object[0]);
        return appName;
    }

    public static Bitmap getAppIcon(Context context, ApplicationInfo appInfo) {
        Log.m2355i(LOG_TAG, "getAppIcon()", new Object[0]);
        return createIcon(context, appInfo, true);
    }

    public static Bitmap getMessageIcon(Context context, ApplicationInfo appInfo) {
        Log.m2355i(LOG_TAG, "getMessageIcon()", new Object[0]);
        return createIcon(context, appInfo, false);
    }

    private static Bitmap createIcon(Context context, ApplicationInfo appInfo, boolean isAppIcon) {
        if (context == null || appInfo == null) {
            return null;
        }
        Bitmap icon;
        Drawable drawable = context.getPackageManager().getApplicationIcon(appInfo);
        if (isAppIcon) {
            icon = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        } else {
            icon = createWhiteBitmap();
        }
        Canvas canvas = new Canvas(icon);

        // make canvas black
        Paint paint2 = new Paint();
        paint2.setColor(Color.BLACK);
        paint2.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint2);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        Log.m2355i(LOG_TAG, "createIcon(), icon width=" + icon.getWidth(), new Object[0]);
        return icon;
    }

    private static Bitmap createWhiteBitmap() {
        Bitmap whiteBitmap = Bitmap.createBitmap(40, 40, Config.RGB_565);
        int[] pixels = new int[1600];
        for (int y = 0; y < 40; y++) {
            for (int x = 0; x < 40; x++) {
                int index = (y * 40) + x;
                int b = (pixels[index] & MotionEventCompat.ACTION_MASK) | MotionEventCompat.ACTION_MASK;
                pixels[index] = ((-16777216 | ((((pixels[index] >> 16) & MotionEventCompat.ACTION_MASK) | MotionEventCompat.ACTION_MASK) << 16)) | ((((pixels[index] >> 8) & MotionEventCompat.ACTION_MASK) | MotionEventCompat.ACTION_MASK) << 8)) | b;
            }
        }
        Log.m2355i(LOG_TAG, "createWhiteBitmap(), pixels num=" + pixels.length, new Object[0]);
        whiteBitmap.setPixels(pixels, 0, 40, 0, 0, 40, 40);
        return whiteBitmap;
    }

    public static boolean isSystemApp(ApplicationInfo appInfo) {
        if ((appInfo.flags & 1) == 0 && (appInfo.flags & 128) == 0) {
            return false;
        }
        return true;
    }

    public static boolean isScreenLocked(Context context) {
        @SuppressLint("WrongConstant") Boolean isScreenLocked = Boolean.valueOf(((KeyguardManager) context.getSystemService("keyguard")).inKeyguardRestrictedInputMode());
        Log.m2355i(LOG_TAG, "isScreenOn(), isScreenOn=" + isScreenLocked, new Object[0]);
        return isScreenLocked.booleanValue();
    }

    public static boolean isScreenOn(Context context) {
        @SuppressLint("WrongConstant") Boolean isScreenOn = Boolean.valueOf(((PowerManager) context.getSystemService("power")).isScreenOn());
        Log.m2355i(LOG_TAG, "isScreenOn(), isScreenOn=" + isScreenOn, new Object[0]);
        return isScreenOn.booleanValue();
    }

    private static Bitmap resizeBitmapByScale(Bitmap bitmap, float widthScale, float heightScale) {
        Log.m2355i(LOG_TAG, "resizeBitmapByScale(), widthScale=" + widthScale + ", heightScale=" + heightScale, new Object[0]);
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap resizeBitmapBySize(Bitmap bitmap, int width, int height) {
        Log.m2355i(LOG_TAG, "resizeBitmapBySize(), width=" + width + ", height=" + height, new Object[0]);
        return resizeBitmapByScale(bitmap, ((float) width) / ((float) bitmap.getWidth()), ((float) height) / ((float) bitmap.getHeight()));
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getFormatedDate() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
    }

    public static int getUtcTime(long localTime) {
        Log.m2355i(LOG_TAG, "getUTCTime(), local time=" + localTime, new Object[0]);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(localTime);
        int utcTime = (int) (cal.getTimeInMillis() / 1000);
        Log.m2355i(LOG_TAG, "getUTCTime(), UTC time=" + utcTime, new Object[0]);
        return utcTime;
    }

    public static int getUtcTimeZone(long localTime) {
        TimeZone tz = TimeZone.getDefault();
        int tzs = tz.getRawOffset();
        if (tz.inDaylightTime(new Date(localTime))) {
            tzs += tz.getDSTSavings();
        }
        Log.m2355i(LOG_TAG, "getUtcTimeZone(), UTC time zone=" + tzs, new Object[0]);
        return tzs;
    }

    public static byte[] getJpgBytes(Bitmap bitmap) {
        Log.m2355i(LOG_TAG, "getJpgBytesFromBitmap()", new Object[0]);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, outStream);
        return outStream.toByteArray();
    }

    public static String getContactName(Context context, String phoneNum) {
        if (phoneNum == null) {
            return null;
        }
        if (phoneNum.equals("")) {
            return null;
        }
        String contactName = phoneNum;
        try {
            Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(contactName));
            Cursor cursor = context.getContentResolver().query(uri, new String[]{}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
            Log.m2355i(LOG_TAG, "getContactName(), contactName=" + contactName, new Object[0]);
            return contactName;
        } catch (Exception e) {
            Log.m2355i(LOG_TAG, "getContactName Exception", new Object[0]);
            return null;
        }
    }

    public static String getKeyFromValue(CharSequence charSequence) {
        Iterator<?> it = AppList.getInstance().getAppList().entrySet().iterator();
        String key = null;
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            if (entry.getValue().equals(charSequence)) {
                key = entry.getKey().toString();
            }
        }
        return key;
    }

    public static long getAvailableStore(String filePath) {
        StatFs statFs = new StatFs(filePath);
        return (((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
    }

    public static byte[] getAlphaJpegImage(Bitmap bitmap) {
        Log.m2355i(LOG_TAG, "getAlphaJpegImage()", new Object[0]);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, bos);
        byte[] out_stream = bos.toByteArray();
        if (!bitmap.hasAlpha()) {
            return out_stream;
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = (w * h) + 2;
        int total_size = (bos.size() + 2) + size;
        int index = 5;
        if (total_size > SupportMenu.USER_MASK) {
            return out_stream;
        }
        try {
            out_stream = new byte[total_size];
            System.arraycopy(bos.toByteArray(), 0, out_stream, 0, 2);
            out_stream[2] = (byte) -1;
            out_stream[3] = (byte) -18;
            out_stream[4] = (byte) (size >> 8);
            out_stream[5] = (byte) (size & MotionEventCompat.ACTION_MASK);
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    index++;
                    out_stream[index] = (byte) Color.alpha(bitmap.getPixel(j, i));
                }
            }
            System.arraycopy(bos.toByteArray(), 2, out_stream, index + 1, bos.size() - 2);
            bos.close();
        } catch (IOException e) {
        }
        return out_stream;
    }
}
