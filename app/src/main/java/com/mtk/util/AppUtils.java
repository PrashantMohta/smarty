package com.mtk.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager.NameNotFoundException;
import com.dandy.smartwatch.modded.R;

public class AppUtils {
    private Context context;

    public AppUtils(Context context) {
        this.context = context;
    }

    public static String getAppName(Context context) {
        try {
            return context.getResources().getString(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return i;
        }
    }

    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getActivityName() {
        String shortClassName = ((RunningTaskInfo) ((ActivityManager) this.context.getSystemService("activity")).getRunningTasks(1).get(0)).topActivity.getShortClassName();
        System.out.println("shortClassName=" + shortClassName);
        return shortClassName;
    }

    public void createShortcut(int resId) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("android.intent.extra.shortcut.NAME", this.context.getString(R.string.app_name));
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra("android.intent.extra.shortcut.INTENT", new Intent("android.intent.action.MAIN").setComponent(new ComponentName(this.context.getPackageName(), "." + ((Activity) this.context).getLocalClassName())));
        shortcut.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", ShortcutIconResource.fromContext(this.context, resId));
        this.context.sendBroadcast(shortcut);
    }
}
