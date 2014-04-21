package com.cui.activity;

import com.cui.test.R;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

public class MyApplication extends Application {

	public static String username = "";
	public static SoundPool soundPool;
	public static int soundid_s1, soundid_s2, soundid_s3, soundid_s4;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initSounds();
	}

	private void initSounds() {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundid_s1 = soundPool.load(getApplicationContext(), R.raw.s1, 1);
		soundid_s2 = soundPool.load(getApplicationContext(), R.raw.s2, 1);
		soundid_s3 = soundPool.load(getApplicationContext(), R.raw.s3, 1);
		soundid_s4 = soundPool.load(getApplicationContext(), R.raw.s4, 1);
	}

	public static void playSound(int soundid) {
		// AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		// int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		soundPool.play(soundid, 1f, 1f, 1, 0, 1f);
	}
}
