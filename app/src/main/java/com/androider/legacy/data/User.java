package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

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
    public static double lati;
    public static double longi;
    public static boolean alreadLogin;
    public static String province;

    public static String tableName = "user";
    public static void store(){
        MainActivity.db.delete(tableName, null, null);
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("nickname", nickname);
        cv.put("password", password);
        cv.put("school", school);
        cv.put("id", id);
        cv.put("major", major);
        cv.put("lati", lati);
        cv.put("longi", longi);
        MainActivity.db.insert(tableName, null, cv);
    }

    public static void positionUser(String accrodingBaidu){
        JSONObject whole;
        try {
            whole = new JSONObject(accrodingBaidu);
            JSONObject point = whole.getJSONObject("content").getJSONObject("point");
            User.longi = point.getDouble("x");
            User.lati = point.getDouble("y");
            User.province = whole.getJSONObject("content").getJSONObject("address_detail").getString("province");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static int resetUser(JSONObject returned){
        try {
            if(returned.getInt("id") == User.id)
                return Constants.userNotReseted;
            User.id = returned.getInt("id");
            User.nickname = returned.getString("nickname");
            User.major = returned.getString("major");
            User.school = returned.getString("school");
            User.lati = returned.getDouble("lati");
            User.longi = returned.getDouble("longi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        store();
        return Constants.userReseted;
    }

    public static void drag(){
        Cursor cursor = MainActivity.db.rawQuery("select * from user;", null);
        if(cursor.isAfterLast()){
            id = -1;
        }else{
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("id"));
            email = cursor.getString(cursor.getColumnIndex("email"));
            password = cursor.getString(cursor.getColumnIndex("password"));
            school = cursor.getString(cursor.getColumnIndex("school"));
            major = cursor.getString(cursor.getColumnIndex("major"));
            nickname = cursor.getString(cursor.getColumnIndex("nickname"));
            longi = cursor.getFloat(cursor.getColumnIndex("longi"));
            lati = cursor.getFloat(cursor.getColumnIndex("lati"));
            cursor.close();
            Holder.peers.put(id, nickname);
        }

        cursor = MainActivity.db.rawQuery("select * from peer;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            int peerId = cursor.getInt(cursor.getColumnIndex("id"));
            String peerNickname = cursor.getString(cursor.getColumnIndex("nickname"));
            Holder.peers.put(peerId, peerNickname);
            cursor.moveToNext();
        }
        cursor.close();
    }

    public static void storePeer(int id, String nickname, double lati, double longi, String school, String major){
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("id", id);
        cv.put("nickname", nickname);
        cv.put("lati", lati);
        cv.put("longi", longi);
        cv.put("school", school);
        cv.put("major", major);
        MainActivity.db.insert("peer", null, cv);
        Holder.peers.put(id, nickname);
    }

    public static String getPeerNick(int id){
        String nickname = Holder.peers.get(id);
        if(nickname == null){
            JSONObject peerInfo;
            try {
                peerInfo = new JSONObject(LegacyClient.getInstance().info(id));
                nickname = peerInfo.getString("nickname");
                Holder.peers.put(id, nickname);
                double lati = peerInfo.getDouble("lati");
                double longi = peerInfo.getDouble("longi");
                String school = peerInfo.getString("school");
                String major = peerInfo.getString("major");
                User.storePeer(peerInfo.getInt("id"), nickname, lati, longi, school, major);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }


}
