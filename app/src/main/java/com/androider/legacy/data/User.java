package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;

/**
 * Created by Think on 2015/4/16.
 */
public class User {
    public static int id;
    public static String nickname;
    public static String email;
    public static String school;
    public static String major;
    public static String password;

    public static String tableName = "user";
    public static void store(){
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("nickname", nickname);
        cv.put("password", password);
        cv.put("school", school);
        cv.put("id", id);
        cv.put("major", major);
        MainActivity.db.insert(tableName, null, cv);
    }

    public static void drag(){
        Cursor cursor = MainActivity.db.rawQuery("select * from user;", null);
        if(cursor.isAfterLast()){
            id = -1;
            return;
        }
        cursor.moveToFirst();
        id = cursor.getInt(cursor.getColumnIndex("id"));
        email = cursor.getString(cursor.getColumnIndex("email"));
        password = cursor.getString(cursor.getColumnIndex("password"));
        school = cursor.getString(cursor.getColumnIndex("school"));
        major = cursor.getString(cursor.getColumnIndex("major"));
        nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        cursor.close();
    }
}
