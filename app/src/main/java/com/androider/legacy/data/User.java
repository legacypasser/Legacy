package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;

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

    public static String tableName = "user";
    public static void store(){
        MainActivity.db.delete(tableName, null , null);
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("nickname", nickname);
        cv.put("password", password);
        cv.put("school", school);
        cv.put("id", id);
        cv.put("major", major);
        MainActivity.db.insert(tableName, null, cv);
    }

    public static void resetUser(JSONObject returned){
        try {
            if(returned.getInt("id") == User.id)
                return;
            User.id = returned.getInt("id");
            User.nickname = returned.getString("nickname");
            User.major = returned.getString("major");
            User.school = returned.getString("school");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        store();
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

    public static void storePeer(int id, String nickname){
        ContentValues cv = new ContentValues();
        cv.clear();
        cv.put("id", id);
        cv.put("nickname", nickname);
        MainActivity.db.insert("peer", null, cv);
        Holder.peers.put(id, nickname);
    }

    public static String getPeerNick(int id){
        String nickname = Holder.peers.get(id);
        if(nickname == null){
            JSONObject peerInfo = null;
            try {
                peerInfo = new JSONObject(LegacyClient.getInstance().info(id));
                User.storePeer(peerInfo.getInt("id"), peerInfo.getString("nickname"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nickname;
    }
}
