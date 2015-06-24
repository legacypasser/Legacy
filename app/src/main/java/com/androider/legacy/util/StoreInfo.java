package com.androider.legacy.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.adapter.IndexAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.User;

/**
 * Created by Think on 2015/6/17.
 */
public class StoreInfo {
    public static final String store = "store";
    public static final String last = "last";
    public static final String lastTime = "lastTime";
    public static final String info = "info";
    public static final String shutter = "shutter";
    public static Boolean getBool(String key){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        return shared.getBoolean(key, false);
    }

    public static void setBool(String key, Boolean value){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void setString(String key, String value){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getString(String key){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        return shared.getString(key, Constants.emptyString);
    }

    public static long getLong(String key){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        return shared.getLong(key, 0);
    }

    public static boolean validLogin(){
        long lastSession = getLong(lastTime);
        return System.currentTimeMillis() - lastSession < 864000000 && getLast();
    }

    public static void setLast(){
        if(validLogin()){
            setLong(lastTime, System.currentTimeMillis());
        }
    }

    private static boolean getLast(){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        return shared.getBoolean(last, false);
    }

    public static void setLong(String key, long value){
        SharedPreferences shared = MainActivity.instance.getSharedPreferences(store, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putLong(key, value);
        editor.apply();
    }
}
