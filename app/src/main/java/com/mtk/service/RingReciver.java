package com.mtk.service;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

public class RingReciver extends Service {
    protected static final String TAG = "RingReciver";
    public static MediaPlayer mMediaPlayer;
    public static Vibrator v;
    public static RingReciver mRingReciver = null;
    private Dialog dialog;
    private Builder localBuilder;

    class C05731 implements OnClickListener {
        C05731() {
        }

        public void onClick(DialogInterface dialog, int which) {
            if (RingReciver.mMediaPlayer.isPlaying()) {
                RingReciver.this.stopSelf();
                v.cancel();
            }
        }
    }

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mRingReciver();
        DialogTipsTitle();
        this.dialog.show();
    }

    public void onDestroy() {
        StopMediaPlayer();
        v.cancel();
        this.dialog.dismiss();
        super.onDestroy();
    }

    public boolean mRingReciver() {
        mMediaPlayer = new MediaPlayer();
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {500, 3000, 500};

        try {
            mMediaPlayer.setDataSource(this, RingtoneManager.getDefaultUri(1));
            //if (((AudioManager) getSystemService("audio")).getStreamVolume(2) != 0) {
                mMediaPlayer.setAudioStreamType(2);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
                v.vibrate(pattern, 0);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setOnCompletionListener(new MediaPlayerComplete(this));
                return true;
            //}
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return false;
    }

    public void StopMediaPlayer() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            v.cancel();

        }
    }

    public IBinder onBind(Intent paramIntent) {
        return null;
    }

    public void onCreate() {
        Log.e(TAG, "Ring Reciver ");
        super.onCreate();
    }

    private void DialogTipsTitle() {
        this.localBuilder = new Builder(this);
        this.localBuilder.setMessage("Smartwatch is looking for phone");
        this.localBuilder.setPositiveButton("okay found it", new C05731());
        this.dialog = this.localBuilder.create();
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.getWindow().setType(2005);
    }
}
