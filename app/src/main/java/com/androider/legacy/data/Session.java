package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.hardware.usb.UsbRequest;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session {
    public int peer;
    public String nickname;
    public String records;

    public static final String tableName = "session";
    public Session(int peer, String nickname, String records) {
        this.peer = peer;
        this.nickname = nickname;
        this.records = records;
    }

    public LinkedList<Record> getRecords(){
        LinkedList<Record> certain = new LinkedList<Record>();
        String[] peerList =  records.split(Constants.regex);
        for(String item : peerList){
            certain.add(Holder.records.get(item));
        }
        return certain;
    }

    public void addRecord(int id){
        if(records == "")
            records += id;
        else
        records += Constants.regex + id;
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

    public static void store(Session item){
        ContentValues cv = new ContentValues();
        cv.put("peer", item.peer);
        cv.put("nickname", item.nickname);
        cv.put("records", item.records);
        MainActivity.db.insert(tableName, null, cv);
    }

    public static void append(Record record){
        int newPeer = record.receiver;
        if(newPeer == User.id)
            newPeer = record.sender;
        if(Holder.peers.get(newPeer) == null){
            try {
                JSONObject peerInfo = new JSONObject(LegacyClient.getInstance().info(newPeer));
                User.storePeer(peerInfo.getInt("id"), peerInfo.getString("nickname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Cursor cursor = MainActivity.db.rawQuery("select * from session where peer = ?;", new String[]{""+newPeer});
        if(cursor.isAfterLast()){
            String nickname = Holder.peers.get(newPeer);
            Session newlyInstall = new Session(newPeer, nickname, "" + record.id);
            Holder.talks.put(newPeer, newlyInstall);
            store(newlyInstall);
        }else {
            cursor.moveToFirst();
            Session existed = getCursored(cursor);
            Session already = Holder.talks.get(existed.peer);
            already.addRecord(record.id);
            ContentValues cv = new ContentValues();
            cv.put("records", already.records);
            MainActivity.db.update(tableName, cv, "peer = ?", new String[]{"" + existed.peer});
        }
        cursor.close();
    }
}
