package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.hardware.usb.UsbRequest;
import android.widget.ArrayAdapter;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session {
    public int peer;
    public String nickname;
    public String records;
    public boolean draged = false;

    public static final String tableName = "session";
    public Session(int peer, String nickname, String records) {
        this.peer = peer;
        this.nickname = nickname;
        this.records = records;
    }

    public ArrayList<Record> dragRecords(){
        Cursor cursor = MainActivity.db.rawQuery("select * from record where receiver = ? or sender = ?;", new String[]{"" + peer, "" + peer});
        cursor.moveToFirst();
        ArrayList<Record> result = new ArrayList<>();
        while (!cursor.isAfterLast()){
            Record item = Record.getCursored(cursor);
            Holder.records.put(item.id, item);
            Holder.belongTo.put(item, this);
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public ArrayList<Record> getRecords(){
        ArrayList<Record> certain = new ArrayList<>();
        String[] peerList =  records.split(Constants.regex);
        if(draged){
            for(String item : peerList){
                certain.add(Holder.records.get(Integer.parseInt(item)));
            }
            return certain;
        }else{
            draged = true;
            return dragRecords();
        }
    }

    public void addRecord(int id){
        if(records == "")
            records += id;
        else
        records += Constants.regex + id;
    }

    public static Session get(int id){
        Session result = Holder.talks.get(id);
        if(result == null){
            result = new Session(id, User.getPeerNick(id), "");
            Holder.talks.put(id, result);
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
                cursor.getString(cursor.getColumnIndex("nickname")),
                cursor.getString(cursor.getColumnIndex("records")));
        return item;
    }

    public void store(){
        ContentValues cv = new ContentValues();
        cv.put("peer", peer);
        cv.put("nickname", nickname);
        cv.put("records", records);
        MainActivity.db.insert(tableName, null, cv);
    }

    public void update(){
        ContentValues cv = new ContentValues();
        cv.put("records", records);
        MainActivity.db.update(tableName, cv, "peer = ?", new String[]{"" + peer});
    }

    public void append(Record record){
        addRecord(record.id);
        Holder.belongTo.put(record, this);
        if(records.equals("")){
            store();
        }else{
            update();
        }
    }
}
