package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Think on 2015/6/4.
 */
public class Mate {
    public int id;
    public String nickname;
    public String school;
    public String major;
    public double lati;
    public double longi;
    public static HashMap<Integer, Mate> peers = new HashMap();

    public static void dragAll(){
        Cursor cursor = MainActivity.db.rawQuery("select * from peer;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Mate one = fromCursor(cursor);
            peers.put(one.id, one);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void store(){
        MainActivity.db.insert("peer", null, getCV());
    }

    public static Mate getPeer(int id){
        Mate mate = peers.get(id);
        if(mate == null){
            mate = fromBase(id);
            if(mate == null){
                mate = fromNet(id);
                mate.store();
            }
            peers.put(id, mate);
        }
        return mate;
    }

    private static Mate fromBase(int id){
        Cursor cursor = MainActivity.db.rawQuery("select * from peer where id = ?;", new String[]{"" + id});
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

    private static Mate fromNet(int id){
        JSONObject peerInfo;
        try {
            peerInfo = new JSONObject(LegacyClient.getInstance().info(id));
            Mate mate= new Mate();
            mate.id = id;
            mate.nickname = peerInfo.getString("nickname");
            mate.lati = peerInfo.getDouble("lati");
            mate.longi = peerInfo.getDouble("longi");
            mate.school = peerInfo.getString("school");
            mate.major = peerInfo.getString("major");
            return mate;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
