package com.androider.legacy.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by baojianting on 2015/4/9.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String dbName = "legacybase";
    public DatabaseHelper(Context context) {
        super(context, dbName, null, 1);
        db = this.getWritableDatabase();
    }
    public static SQLiteDatabase db;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE  post  (" +
                "   id  INT primary key," +
                "   des  VARCHAR," +
                "   img  VARCHAR," +
                "   seller  INT," +
                "   publish  TIMESTAMP," +
                "   abs  VARCHAR," +
                "   price int," +
                "   type int);");

        db.execSQL("CREATE TABLE session ( peer INT primary key, nickname VARCHAR);");
        db.execSQL("CREATE TABLE record ( id INTEGER, sender INT, receiver INT, content VARCHAR, edit TIMESTAMP);");
        db.execSQL("CREATE TABLE user (id INT primary key, nickname VARCHAR, email VARCHAR, password VARCHAR, school VARCHAR, major VARCHAR, lati FLOAT, longi FLOAT);");
        db.execSQL("CREATE TABLE peer ( id INT primary key, nickname varchar, lati DOUBLE, longi DOUBLE , school VARCHAR, major VARCHAR);");
        db.execSQL("CREATE TABLE nick_adj(id INTEGER, adj VARCHAR);");
        db.execSQL("CREATE TABLE nick_noun(id INTEGER, noun VARCHAR)");
        db.execSQL("CREATE TABLE school(id INTEGER PRIMARY KEY, name VARCHAR, region INT, major VARCHAR)");
        db.execSQL("CREATE TABLE major(id INT primary key, name VARCHAR)");
        db.execSQL("CREATE TABLE region(id INT primary key, name VARCHAR)");
        db.execSQL("CREATE TABLE req(url VARCHAR primary key, content VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
