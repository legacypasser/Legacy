package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.NetConstants;
import com.jialin.chat.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Think on 2015/4/16.
 */
public class Record {
    public int sender;
    public int receiver;
    public String content;
    public long edit;

    public static final String tableName = "record";

    public Record(int sender, int receiver, String content, long edit) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.edit = edit;
    }

    public Record(int sender, int receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        edit = System.currentTimeMillis();
    }

    public static Record getCursored(Cursor cursor){
        return new Record(cursor.getInt(cursor.getColumnIndex("sender")),
                cursor.getInt(cursor.getColumnIndex("receiver")),
                cursor.getString(cursor.getColumnIndex("content")),
                cursor.getLong(cursor.getColumnIndex("edit"))
        );
    }

    public static ArrayList<Record> strToList(String str){
        ArrayList<Record> result = new ArrayList<>();
        try {
            JSONArray jsonRecords= new JSONArray(str);
            for(int i = 0; i < jsonRecords.length(); i++){
                result.add(strToObj(jsonRecords.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Record> getOnline(){
        String allMsg = LegacyClient.getInstance().online();
        if(allMsg.equals("empty"))
            return null;
        return strToList(allMsg);
    }

    public static Record fromCome(String[] tokens){
        int senderId = Integer.parseInt(tokens[1]);
        int receiverId = Integer.parseInt(tokens[2]);
        long comeEdit = Long.parseLong(tokens[3]);
        String comeContent = tokens[4];
        return new Record(senderId,receiverId,comeContent,comeEdit);
    }

    public String formFeedBack(){
        return "" + sender + NetConstants.regex + receiver + NetConstants.regex + edit;
    }

    public static Record strToObj(String str){
        try {
            JSONObject jObj = new JSONObject(str);
            return new Record(
                    jObj.getInt("sender"),
                    jObj.getInt("receiver"),
                    jObj.getString("content"),
                    PostConverter.formater.parse(jObj.getString("edit")).getTime()
            );
        } catch (JSONException|ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void store(){
        MainActivity.db.insert(tableName, null, getCV());
    }

    public ContentValues getCV(){
        ContentValues cv = new ContentValues();
        cv.put("sender", this.sender);
        cv.put("receiver", this.receiver);
        cv.put("content", this.content);
        cv.put("edit", this.edit);
        return cv;
    }

    public Session getSession(){
        int newPeer = (receiver == User.id)? sender: receiver;
        Session owner = Session.get(newPeer);
        return owner;
    }

    public void moreCome(){
        store();
        if(getSession().draged)
            getSession().append(this);

    }

    @Override
    public String toString(){
        return "" + sender + NetConstants.regex + receiver + NetConstants.regex + edit + NetConstants.regex + content;
    }

    public Message formMessage(){
        return new Message(0, 1, Holder.peers.get(sender), "avatar", Holder.peers.get(receiver), "avatar", content, User.id == sender, true, new Date(edit));
    }
}
