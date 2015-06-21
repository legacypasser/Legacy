package com.androider.legacy.util;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.net.Uri;

import com.androider.legacy.activity.MainActivity;

/**
 * Created by Think on 2015/6/20.
 */
public class SoundShouter {

    public static void playInfo(){
        if(!StoreInfo.getBool(StoreInfo.info))
            return;
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(MainActivity.instance.getApplicationContext(), notification);
        r.play();
    }

}
