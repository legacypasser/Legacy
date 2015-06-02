package com.androider.legacy.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androider.legacy.activity.MainActivity;

import java.net.NetworkInterface;

/**
 * Created by Think on 2015/5/31.
 */
public class ConnectDetector {
    public static boolean isConnectedToNet(){
        ConnectivityManager manager = (ConnectivityManager) MainActivity.instance.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info != null)
            return info.isAvailable();
        return false;
    }
}
