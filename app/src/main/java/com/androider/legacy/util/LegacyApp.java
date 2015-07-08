package com.androider.legacy.util;

import android.app.Application;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Nicker;
import com.androider.legacy.data.School;
import com.androider.legacy.data.User;
import com.androider.legacy.database.DatabaseHelper;

/**
 * Created by Think on 2015/6/25.
 */
public class LegacyApp extends Application {
    public static LegacyApp instance;
    private static final String inited = "inited";
    public static String filePath;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new DatabaseHelper(this);
        User.instance.drag();
        filePath = this.getApplicationContext().getFilesDir() + "/";
        if(!StoreInfo.getBool(inited)){
            Nicker.initNick(instance);
            School.iniSchool(instance);
            StoreInfo.setBool(inited, true);
        }
    }
}
