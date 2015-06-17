package com.androider.legacy.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.adapter.IndexAdapter;

/**
 * Created by Think on 2015/6/17.
 */
public class StoreInfo {
    public static final String store = "store";
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
        return shared.getString(key, null);
    }
}
