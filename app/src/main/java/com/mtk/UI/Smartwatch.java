package com.mtk.UI;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Activity;

import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dandy.smartwatch.modded.R;
import com.mtk.data.PreferenceData;
import com.mtk.remotecamera.Preview;
import com.mtk.remotecamera.RemoteCameraService;
import com.mtk.service.MainService;
import com.mtk.util.BluetoothUtils;
import com.mtk.util.CommUtil;
import com.mtk.util.LogUtils;
import com.mtk.util.ToastUtils;

import android.view.View;
import android.provider.Settings;


import java.io.ByteArrayOutputStream;

import static com.mtk.btconnection.BluetoothManager.BT_BROADCAST_ACTION;
import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;


public class Smartwatch extends Activity implements Handler.Callback {
    protected Context context;
    protected Handler mHandler;
    private AlertDialog enableNotificationListenerAlertDialog;
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    TextView status;
    ToggleButton NotificationToggle ;
    ToggleButton SMSToggle ;
    ToggleButton PrivacyToggle ;

    AutoCompleteTextView URL;
    WebView previewWebview;
    public boolean handleMessage(Message msg) {
        LogUtils.m2364i("Smartwatch", "msg.arg1=" + msg.arg1);
        switch (msg.what) {
            case 3:
                LogUtils.m2364i("Smartwatch", "msg.arg1=" + msg.arg1);
                setBTLinkState();
                break;
        }
        return false;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setBTLinkState();
        }
    };


        public void Ntoggle(View v)
        {
            // Is the toggle on
            boolean on = ((ToggleButton) v).isChecked();
            if (on) {
                PreferenceData.setNotificationServiceEnable(true);
            } else {
                PreferenceData.setNotificationServiceEnable(false);
            }
        }

    public void Ptoggle(View v)
    {
        // Is the toggle on
        boolean on = ((ToggleButton) v).isChecked();
        if (on) {
            PreferenceData.setNotificationPrivate(true);
        } else {
            PreferenceData.setNotificationPrivate(false);
        }
    }

    public void RestartService(View v)
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
        ToastUtils.showShortToast(context,"Restarting...");
        recreate();
    }
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }

        setContentView(R.layout.activity_smartwatch);
        status= (TextView) findViewById(R.id.textView2);
        NotificationToggle = (ToggleButton) findViewById(R.id.NotificationToggle);
        NotificationToggle.setChecked(PreferenceData.isNotificationServiceEnable());
        SMSToggle = (ToggleButton) findViewById(R.id.SMSToggle);
        SMSToggle.setChecked(PreferenceData.isSmsServiceEnable());
        PrivacyToggle =(ToggleButton) findViewById(R.id.PrivacyToggle);
        PrivacyToggle.setChecked(PreferenceData.isNotificationPrivate());
        URL= (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        URL.setText(PreferenceData.getAppURL());

        previewWebview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = previewWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        previewWebview.setWebViewClient(new WebViewClient());
        previewWebview.getSettings().setGeolocationEnabled(true);
        previewWebview.setWebChromeClient(new GeoWebChromeClient());
        previewWebview.getSettings().setGeolocationDatabasePath( getApplicationContext().getFilesDir().getPath() );

        previewWebview.measure(240, 240);
        previewWebview.layout(0, 0, 720, 720);
        previewWebview.loadUrl(PreferenceData.getAppURL());

        this.context=this;
        this.mHandler = new Handler(this);

        setBTLinkState();
        IntentFilter filter = new IntentFilter(BT_BROADCAST_ACTION);
        registerReceiver(mReceiver, filter);

        if(!CommUtil.isAccessibilitySettingsOn(this.context)||true) {
            if (PreferenceData.isNotificationServiceEnable() && !MainService.isNotificationServiceActived()) {
                CommUtil.showAccessibilityPrompt(this.context);
            }
        }
        if(!isNotificationServiceEnabled()){
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        if (PreferenceData.isSmsServiceEnable() || PreferenceData.isNotificationServiceEnable()) {
            startMainService();
        }
    }
    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if eanbled, false otherwise.
     */
    private boolean isNotificationServiceEnabled(){
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private AlertDialog buildNotificationServiceAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notification Listener Service");
        alertDialogBuilder.setMessage("For the the app. to work you need to enable the Notification Listener Service. Enable it now?");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // If you choose to not enable the notification listener
                        // the app. will not work as expected
                    }
                });
        return(alertDialogBuilder.create());
    }
    private void startMainService() {
        startService(new Intent(this.context, MainService.class));
    }

    private void setBTLinkState() {
        if (MainService.mBluetoothManager.isBTConnected()) {
            status.setText("SmartWatch : Connected");
        }
        else
        {
            status.setText("SmartWatch : Disconnected");
        }
    }

    public void settings(View v) {

        String url = URL.getText().toString();
        PreferenceData.setAppURL(url);
        //ToastUtils.showShortToast(this.context,"will open"+url);
        previewWebview.loadUrl(PreferenceData.getAppURL());


    }

    public void findWatch(View v) {
        String cmdOfLista = String.valueOf(6) + " 0 ";
        if (BluetoothUtils.getBlutootnLinkState().booleanValue() && MainService.mBluetoothManager.isBTConnected()) {
            MainService.getInstance().sendCAPCResult(cmdOfLista);
            ToastUtils.showShortToast(this.context,"Watch is making sound!");
        } else {
            BluetoothUtils.repairBluetooth(this.context);
        }
    }

    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
