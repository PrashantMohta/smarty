package com.mtk.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Build.VERSION;
import android.support.v4.view.MotionEventCompat;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetUtils {
    private NetUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isNetConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.getState() == State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        boolean z = true;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            ToastUtils.showShortToast(context, (CharSequence) "Need a Wifi network !");
            return false;
        }
        if (networkInfo.getType() != 1) {
            z = false;
        }
        return z;
    }

    public static void openSetting(Context context, boolean isNetSetting) {
        Intent intent;
        if (VERSION.SDK_INT <= 10) {
            intent = new Intent("android.settings.WIRELESS_SETTINGS");
        } else if (isNetSetting) {
            intent = new Intent("android.settings.WIRELESS_SETTINGS");
        } else {
            intent = new Intent("android.settings.SETTINGS");
        }
        context.startActivity(intent);
    }

    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & MotionEventCompat.ACTION_MASK).append(".");
        sb.append((ipInt >> 8) & MotionEventCompat.ACTION_MASK).append(".");
        sb.append((ipInt >> 16) & MotionEventCompat.ACTION_MASK).append(".");
        sb.append((ipInt >> 24) & MotionEventCompat.ACTION_MASK);
        return sb.toString();
    }

    public static String getLocalIpAddress(Context context) {
        if (isNetConnected(context)) {
            if (isWifiConnected(context)) {
                return int2ip(((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getIpAddress());
            }
            try {
                Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                while (en.hasMoreElements()) {
                    Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
