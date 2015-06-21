package com.androider.legacy.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.androider.legacy.activity.MainActivity;

public class NetStateReceiver extends BroadcastReceiver {
    public NetStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(!mobileInfo.isConnected() && !wifiInfo.isConnected())
            MainActivity.instance.chatOff();
        else
            MainActivity.instance.chatOn();
    }
}
