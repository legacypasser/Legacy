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
        db.execSQL("CREATE TABLE  post  (" +
                "   id  INTEGER PRIMARY KEY," +
                "   des  VARCHAR," +
                "   img  VARCHAR," +
                "   seller  INTEGER," +
                "   publish  TIMESTAMP," +
                "   abs  VARCHAR" +
                ");");
        db.execSQL("CREATE TABLE session (peer INTEGER PRIMARY KEY, nickname VARCHAR, records VARCHAR);");
        db.execSQL("CREATE TABLE record (id INTEGER PRIMARY KEY, sender INTEGER, receiver INTEGER, content VARCHAR, edit TIMESTAMP);");
        db.execSQL("CREATE TABLE user (id INTEGER PRIMARY KEY, nickname VARCHAR, email VARCHAR, password VARCHAR, school VARCHAR, major VARCHAR);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
