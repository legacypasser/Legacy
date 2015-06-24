package com.androider.legacy.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.net.LegacyClient;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Think on 2015/4/16.
 */
public class User extends Mate{
    public String email;
    public String password;
    public String province;
    public static final String tableName = "user";
    public static User instance = new User();

    private User(){

    }

    public void store(){
        DatabaseHelper.db.delete(tableName, null, null);
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("nickname", nickname);
        cv.put("password", password);
        cv.put("school", school);
        cv.put("id", id);
        cv.put("major", major);
        cv.put("lati", lati);
        cv.put("longi", longi);
        DatabaseHelper.db.insert(tableName, null, cv);
    }

    public void positionUser(String accrodingBaidu){
        JSONObject whole;
        try {
            whole = new JSONObject(accrodingBaidu);
            JSONObject point = whole.getJSONObject("content").getJSONObject("point");
            longi = point.getDouble("x");
            lati = point.getDouble("y");
            province = whole.getJSONObject("content").getJSONObject("address_detail").getString("province");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int resetUser(JSONObject returned){
        try {
            if(returned.getInt("id") == id)
                return Constants.userNotReseted;
            id = returned.getInt("id");
            nickname = returned.getString("nickname");
            major = returned.getString("major");
            school = returned.getString("school");
            lati = returned.getDouble("lati");
            longi = returned.getDouble("longi");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        store();
        return Constants.userReseted;
    }

    public void drag(){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from user;", null);
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
        }
        Mate.dragAll();
    }


}
