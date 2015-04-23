package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;

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
    public int id;
    public int sender;
    public int receiver;
    public String content;
    public Date edit;

    public static final String tableName = "record";

    public Record(int id, int sender, int receiver, String content, Date edit) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.edit = edit;
    }

    public static ArrayList<Record> drag(int peer){
        Cursor cursor = MainActivity.db.rawQuery("select * from record;", new String[]{"" + peer, "" + peer});
        cursor.moveToFirst();
        ArrayList<Record> result = new ArrayList<>();
        while (!cursor.isAfterLast()){
            Record item = getCursored(cursor);
            Holder.records.put(item.id, item);
            result.add(item);
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public static Record getCursored(Cursor cursor){
        Record item = new Record(cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getInt(cursor.getColumnIndex("sender")),
                cursor.getInt(cursor.getColumnIndex("receiver")),
                cursor.getString(cursor.getColumnIndex("content")),
                new Date(cursor.getLong(cursor.getColumnIndex("edit")))
        );
        return item;
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
        ArrayList<Record> result = strToList(allMsg);
        for(Record item : result){
            moreCome(item);
        }
        return result;
    }

    public static Record strToObj(String str){
        try {
            JSONObject jObj = new JSONObject(str);
            Record item = new Record(
                    jObj.getInt("id"),
                    jObj.getInt("sender"),
                    jObj.getInt("receiver"),
                    jObj.getString("content"),
                    PostConverter.formater.parse(jObj.getString("edit"))
            );
            return item;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void store(Record item){
        MainActivity.db.insert(tableName, null, getCV(item));
    }

    public static ContentValues getCV(Record item){
        ContentValues cv = new ContentValues();
        cv.put("id", item.id);
        cv.put("sender", item.sender);
        cv.put("receiver", item.receiver);
        cv.put("content", item.content);
        cv.put("edit", item.edit.getTime());
        return cv;
    }

    public static void moreCome(Record record){
        Holder.records.put(record.id, record);
        store(record);
        Session.append(record);
    }
}
