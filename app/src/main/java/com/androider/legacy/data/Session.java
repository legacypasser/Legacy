package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.hardware.usb.UsbRequest;
import android.widget.ArrayAdapter;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session {
    public int peer;
    public String nickname;
    public ArrayList<Record> records;
    public boolean draged = false;
    public boolean affected =false;

    public static final String tableName = "session";
    public Session(int peer, String nickname) {
        this.peer = peer;
        this.nickname = nickname;
        records = new ArrayList<>();
    }

    public void dragRecords(){
        Cursor cursor = MainActivity.db.rawQuery("select * from record where receiver = ? or sender = ? order by edit asc;", new String[]{"" + peer, "" + peer});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Record item = Record.getCursored(cursor);
            records.add(item);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public ArrayList<Record> getRecords(){
        if(draged)
            return records;
        else {
            dragRecords();
            draged = true;
            return records;
        }
    }

    public static Session get(int id){
        Session result = Holder.talks.get(id);
        if(result == null){
            result = new Session(id, User.getPeerNick(id));
            Holder.talks.put(id, result);
            result.store();
        }
        return result;
    }

    public static void drag(){
        Cursor cursor = MainActivity.db.rawQuery("select * from session;", null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Session item = getCursored(cursor);
            Holder.talks.put(item.peer, item);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public static Session getCursored(Cursor cursor){
        Session item = new Session(
                cursor.getInt(cursor.getColumnIndex("peer")),
                cursor.getString(cursor.getColumnIndex("nickname")));
        return item;
    }

    public void store(){
        ContentValues cv = new ContentValues();
        cv.put("peer", peer);
        cv.put("nickname", nickname);
        MainActivity.db.insert(tableName, null, cv);
    }

    public void append(Record record){
        records.add(record);
        affected = true;
    }

    public long getLast(){
        if(records.isEmpty())
            return 0;
        return records.get(records.size() - 1).edit;
    }
}
