package com.mtk.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import com.dandy.smartwatch.modded.R;

public class BluetoothUtils {
    private static final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public static void openOrCloseBT(Context context) {
        if (mBluetoothAdapter == null) {
            ToastUtils.showShortToast(context, context.getResources().getString(R.string.no_found_drivers));
        } else if (mBluetoothAdapter.isEnabled()) {
            CloseBluetooth();
            ToastUtils.showShortToast(context, context.getResources().getString(R.string.bluetooth_has_been_closed));
        } else {
            OpenBluetooth(context);
        }
    }

    public static void OpenBluetooth(Context context) {
        if (!mBluetoothAdapter.isEnabled()) {
            context.startActivity(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"));
        }
    }

    private static void CloseBluetooth() {
        mBluetoothAdapter.disable();
    }

    public static void repairBluetooth(Context context) {
        context.startActivity(new Intent("android.settings.BLUETOOTH_SETTINGS"));
    }

    public static Boolean getBlutootnLinkState() {
        boolean z = false;
        if (mBluetoothAdapter == null) {
            return Boolean.valueOf(false);
        }
        if (mBluetoothAdapter.getState() != 10) {
            z = true;
        }
        return Boolean.valueOf(z);
    }
}
