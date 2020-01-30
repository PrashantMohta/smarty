package com.mtk.util;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;

import com.dandy.smartwatch.modded.R;

public class CommUtil {

    static class C05791 implements OnClickListener {
        C05791() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        if (accessibilityEnabled == 1) {
            return true;
        }
        return false;
    }
    public static void showAccessibilityPrompt(final Context context) {
        Builder builder = new Builder(context);
        builder.setTitle(R.string.accessibility_prompt_title);
        builder.setMessage(R.string.accessibility_prompt_content);
        builder.setNegativeButton(R.string.cancel, new C05791());
        builder.setPositiveButton(R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.startActivity(new Intent("android.settings.ACCESSIBILITY_SETTINGS"));
            }
        });
        builder.create().show();
    }
}
