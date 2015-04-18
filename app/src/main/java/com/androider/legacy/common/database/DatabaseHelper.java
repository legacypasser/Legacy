package com.androider.legacy.common.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.androider.legacy.card.model.CardInfo;

import java.util.List;

/**
 * Created by baojianting on 2015/4/9.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String dbName = "legacybase";
    public DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table post ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // TODO: 完成这个方法
    public List<CardInfo> queryCardInfoByTimeDesc() {
        return null;
    }
}
