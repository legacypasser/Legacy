package com.androider.legacy.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;

import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by Think on 2015/5/14.
 */
public class Nicker {
    public static int getAdjTotal(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from nick_adj", null);
        int adjTotal = cursor.getCount();
        cursor.close();
        return adjTotal;
    }

    public static int getNounTotal(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from nick_noun;", null);
        int nounTotal = cursor.getCount();
        cursor.close();
        return nounTotal;
    }

    public static void initNick(Context context){
        if(getAdjTotal() != 0 || getNounTotal() != 0)
            return;
        AssetManager manager = context.getAssets();
        DatabaseHelper.db.beginTransaction();
        try {
            InputStream in = manager.open("adj.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String str;
            ContentValues cv;
            while ((str = reader.readLine()) != null){
                cv = new ContentValues();
                cv.put("adj", str);
                DatabaseHelper.db.insert("nick_adj", null, cv);
            }
            reader.close();
            in.close();
            in = manager.open("noun.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            while ((str = reader.readLine()) != null){
                cv = new ContentValues();
                cv.put("noun", str);
                DatabaseHelper.db.insert("nick_noun", null, cv);
            }
            reader.close();
            in.close();
            DatabaseHelper.db.setTransactionSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            DatabaseHelper.db.endTransaction();
        }
    }

    public static String getAdj(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select adj from nick_adj;", null);
        cursor.move(new Random().nextInt(getAdjTotal()));
        String adj = cursor.getString(0);
        cursor.close();
        return adj;
    }
    public static String getNoun(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select noun from nick_noun;", null);
        cursor.move(new Random().nextInt(getNounTotal()));
        String noun = cursor.getString(0);
        cursor.close();
        return noun;
    }
}
