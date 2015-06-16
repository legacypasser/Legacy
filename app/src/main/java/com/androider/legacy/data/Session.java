package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.hardware.usb.UsbRequest;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.ArrayAdapter;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.net.LegacyClient;
import com.jialin.chat.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Session implements Serializable{
    public int peer;
    public String nickname;
    public ArrayList<Record> records;
    public boolean draged = false;
    public int owner;

    public static final String tableName = "session";
    public Session(int peer, String nickname) {
        this.peer = peer;
        this.nickname = nickname;
        records = new ArrayList<>();
    }

    public void dragRecords(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from record where receiver = ? or sender = ? order by edit asc;", new String[]{"" + peer, "" + peer});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Record item = Record.getCursored(cursor);
            records.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        draged = true;
    }

    public ArrayList<Record> getRecords(){
        if(draged)
            return records;
        else {
            dragRecords();
            return records;
        }
    }

    public static Session get(Mate item){
        Session one = Holder.talks.get(item.id);
        if(one != null)
            return one;
        one = new Session(item.id, item.nickname);
        one.owner = User.instance.id;
        one.draged = true;
        Holder.talks.put(item.id, one);
        one.store();
        android.os.Message msg = android.os.Message.obtain();
        try {
            msg.what = Constants.emptySession;
            Bundle data = new Bundle();
            data.putSerializable(Constants.chat, one);
            msg.setData(data);
            new Messenger(MainActivity.instance.netHandler).send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return one;
    }

    public static ArrayList<Session> get(ArrayList<Mate> mates){
        ArrayList<Session> created = new ArrayList<>();
        for(Mate item : mates){
            created.add(get(item));
        }
        return created;
    }

    public static void drag(){
        if(User.instance.id == -1)
            return;
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from session where owner = ?;", new String[]{"" + User.instance.id});
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
        item.owner = User.instance.id;
        return item;
    }

    public void store(){
        ContentValues cv = new ContentValues();
        cv.put("peer", peer);
        cv.put("owner", owner);
        cv.put("nickname", nickname);
        DatabaseHelper.db.insert(tableName, null, cv);
    }

    public void append(Record record){
        records.add(record);
    }

    public long getLast(){
        if(records.isEmpty())
            return 0;
        return records.get(records.size() - 1).edit;
    }
}
