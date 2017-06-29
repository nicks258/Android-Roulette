package com.zhy.sample_circlemenu.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.media.SoundPool.Builder;
import android.os.Build.VERSION;

public class SoundManager {
    public static final String PREFS_NAME = "DecideNowPrefs";
    public static final String SOUND_MUTED_KEY = "soundMutedKey";
    private static SoundManager ourInstance = new SoundManager();
    private Context context;
    private boolean muted = false;
    protected SoundPool pool;

    public static SoundManager getInstance() {
        return ourInstance;
    }

    private SoundManager() {
    }

    public SoundManager init(Context c) {
        this.context = c;
        if (VERSION.SDK_INT >= 21) {
            this.pool = createSoundPool();
        } else {
            this.pool = createSoundPoolLegacy();
        }
        updateMuted();
        return this;
    }

    @TargetApi(21)
    private SoundPool createSoundPool() {
        Builder builder = new Builder();
        builder.setMaxStreams(6);
        builder.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
        return new Builder().build();
    }

    private SoundPool createSoundPoolLegacy() {
        return new SoundPool(6, 3, 0);
    }

    protected void updateMuted() {
        this.muted = this.context.getSharedPreferences("DecideNowPrefs", 0).getBoolean(SOUND_MUTED_KEY, false);
    }

    public boolean getMuted() {
        return this.muted;
    }

    public void setMuted(boolean m) {
        if (m != this.muted) {
            this.muted = m;
            Editor editor = this.context.getSharedPreferences("DecideNowPrefs", 0).edit();
            editor.putBoolean(SOUND_MUTED_KEY, this.muted);
            editor.apply();
        }
    }

    public int load(Context context, int resId, int priority) {
        return this.pool.load(context, resId, priority);
    }

    public int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        if (!getMuted()) {
            this.pool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
        return 0;
    }

    public void toggleMute() {
        setMuted(!getMuted());
    }

    public void unload(int soundId) {
        this.pool.unload(soundId);
    }
}
