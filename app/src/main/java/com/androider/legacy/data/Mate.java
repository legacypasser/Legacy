package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Think on 2015/6/4.
 */
public class Mate implements Serializable{
    public int id;
    public String nickname;
    public String school;
    public String major;
    public double lati;
    public double longi;
    public static HashMap<Integer, Mate> peers = new HashMap();

    public static void dragAll(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from peer;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Mate one = fromCursor(cursor);
            peers.put(one.id, one);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void store(){
        DatabaseHelper.db.insert("peer", null, getCV());
    }

    public static Mate getPeer(int id){
        Mate mate = peers.get(id);
        if(mate == null){
            mate = fromBase(id);
            if(mate == null)
                return null;
            peers.put(id, mate);
        }
        return mate;
    }

    public static Mate justGet(int id){
        String result = LegacyClient.getInstance().get(getUrl(id));
        return fromString(result);
    }

    private static String getUrl(int id){
        return Constants.requestPath + Constants.info + Constants.ask + Constants.id + id;
    }

    public static void getPeer(int id, LegacyTask.RequestCallback callback){
        LegacyClient.getInstance().callTask(getUrl(id), callback);
    }

    private static Mate fromBase(int id){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from peer where id = ?;", new String[]{"" + id});
        cursor.moveToFirst();
        if(cursor.isAfterLast()){
            cursor.close();
            return null;
        }
        Mate result = fromCursor(cursor);
        cursor.close();
        return result;
    }

    private ContentValues getCV(){
        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("nickname", nickname);
        cv.put("lati", lati);
        cv.put("longi", longi);
        cv.put("school", school);
        cv.put("major", major);
        return cv;
    }

    private static Mate fromCursor(Cursor cursor){
        Mate mate = new Mate();
        mate.id = cursor.getInt(cursor.getColumnIndex("id"));
        mate.lati = cursor.getDouble(cursor.getColumnIndex("lati"));
        mate.longi = cursor.getDouble(cursor.getColumnIndex("longi"));
        mate.nickname = cursor.getString(cursor.getColumnIndex("nickname"));
        mate.major = cursor.getString(cursor.getColumnIndex("major"));
        mate.school = cursor.getString(cursor.getColumnIndex("school"));
        return mate;
    }

    public static ArrayList<Mate> stringToList(String result){
        ArrayList<Mate> mates = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(result);
            for(int i = 0; i < array.length(); i++){
                Mate mate = fromString(array.getString(i));
                mate.store();
                mates.add(mate);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mates;
    }

    public static Mate fromString(String str){
        JSONObject peerInfo;
        try {
            peerInfo = new JSONObject(str);
            Mate mate= new Mate();
            mate.id = peerInfo.getInt("id");
            mate.nickname = peerInfo.getString("nickname");
            mate.lati = peerInfo.getDouble("lati");
            mate.longi = peerInfo.getDouble("longi");
            mate.school = peerInfo.getString("school");
            mate.major = peerInfo.getString("major");
            mate.store();
            Mate.peers.put(mate.id, mate);
            return mate;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
