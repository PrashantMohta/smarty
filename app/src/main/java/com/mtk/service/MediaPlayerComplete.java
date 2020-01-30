package com.mtk.service;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

class MediaPlayerComplete implements OnCompletionListener {
    MediaPlayerComplete(RingReciver paramRingReciver) {
    }

    public void onCompletion(MediaPlayer paramMediaPlayer) {
        RingReciver.mMediaPlayer.release();
    }
}
