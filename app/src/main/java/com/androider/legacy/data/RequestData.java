package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;

/**
 * Created by Think on 2015/5/31.
 */
public class RequestData {
    public static String fromBase(String url){
        Cursor cursor = MainActivity.db.rawQuery("select * from req where url = ?;", new String[]{url});
        cursor.moveToFirst();
        if(cursor.isAfterLast()){
            cursor.close();
            return null;
        }
        RequestData req = new RequestData();
        String content = cursor.getString(cursor.getColumnIndex("content"));
        cursor.close();
        return content;
    }

    public static void save(String url, String content){
        Cursor cursor = MainActivity.db.rawQuery("select content from req where url = ?;", new String[]{url});
        cursor.moveToFirst();
        ContentValues cv = new ContentValues();
        cv.put("content", content);
        if(cursor.isAfterLast()){
            cv.put("url", url);
            MainActivity.db.insert("req", null, cv);
        }else{
            MainActivity.db.update("req", cv, "url = ?", new String[]{url});
        }
        cursor.close();
    }
}
