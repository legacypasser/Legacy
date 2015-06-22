package com.androider.legacy.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.service.NetService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Think on 2015/4/16.
 */

public class Post{
    public int id;
    public int price;
    public String des;
    public String img;
    public int seller;
    public Date publish;
    public String school;
    public String abs;
    public int type;

    public static final String tableName = "post";
    public static final int selfType = 1;
    public static final int doubanType = 0;

    public String getAbsImg(){
        if(type == doubanType){
            return img;
        }else{
            String name = img.split(";")[0];
            String path = "file://" + MainActivity.filePath + "s_" + name;
            if(fileExists(path))
                return path;
            else
                return Constants.imgPath + "s_" + name;
        }
    }

    private boolean fileExists(String path){
        File file = new File(path);
        return file.exists();
    }

    public static ArrayList<Post> arryToList(ArrayList<JSONObject> objs){
        ArrayList<Post> result = new ArrayList<>();
        try {
            for(JSONObject item : objs){
                int id = item.getInt("id");
                String img = item.getString("img");
                Date publish = new Date(item.getLong("publish"));
                String abs = item.getString("abs");
                String school = item.getString("school");
                int price = item.getInt("price");
                int seller = item.getInt("seller");
                Post added = new Post(id, img, publish, abs, price, seller);
                added.type = item.getInt("type");
                added.school = school;
                result.add(added);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static ArrayList<Post> stringToList(String str){
        ArrayList<JSONObject> objs = new ArrayList<>();
        try {
            JSONArray jsonPosts = new JSONArray(str);
            for(int i = 0; i < jsonPosts.length(); i++){
                objs.add(jsonPosts.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arryToList(objs);
    }

    public ArrayList<String> getDetailImg(){
        ArrayList<String> imgs = new ArrayList<>();
        if(type == doubanType){
            String big = img.replace("mpic", "lpic");
            imgs.add(big);
        }else if(type == selfType){
            String[] all = img.replace("mpic", "lpic").split(";");
                for(String item : all){
                    String path = "file://" + MainActivity.filePath + item;
                    if(fileExists(path))
                        imgs.add(path);
                    else
                        imgs.add(Constants.imgPath + item);
                }
        }
        return imgs;
    }

    public Post(int id, String img, Date publish, String abs, int price, int seller) {
        this.id = id;
        this.des = "";
        this.img = img;
        this.publish = publish;
        this.abs = abs;
        this.price = price;
        this.seller = seller;
        this.school = "δ֪ѧУ";
    }

    public Post(int id,String des, String img, int seller, Date publish, String abs, int price) {
        this.id = id;
        this.des = des;
        this.img = img;
        this.seller = seller;
        this.publish = publish;
        this.abs = abs;
        this.price = price;
        this.school = "δ֪ѧУ";
    }

    public static Post get(int id){
        Post certain = Holder.detailed.get(id);
        if(certain == null){
            certain = detailFromBase(id);
            Holder.detailed.put(id, certain);
        }
        return certain;
    }
    public static void get(int id, LegacyTask.RequestCallback callback) {
        String url = LegacyClient.getInstance().getRestUrl(id);
        LegacyClient.getInstance().callTask(url, callback);
    }

    public JSONObject toServerJson(){
        JSONObject rawObj = new JSONObject();
        try {
            rawObj.put("abs", abs);
            rawObj.put("price", price);
            rawObj.put("des", des);
            rawObj.put("img", img);
            rawObj.put("type", type);
            rawObj.put("school", school);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rawObj;
    }

    public JSONObject toAliJson(){
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("des", des);
            data.put("abs", abs);
            data.put("img", img);
            data.put("price", price);
            data.put("seller", seller);
            data.put("publish", publish.getTime());
            data.put("type", type);
            data.put("school", school);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static Post detailFromBase(int id){
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from post where id=?;", new String[]{"" + id});
        cursor.moveToFirst();
        Post result = getCursored(cursor);
        cursor.close();
        Holder.detailed.put(id, result);
        return result;
    }

    public static ArrayList<Post> listFromBase(int seller){
        ArrayList<Post> result = new ArrayList<Post>();
        Cursor cursor = DatabaseHelper.db.rawQuery("select * from post where seller = ?;", new String[]{"" + seller});
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            result.add(getCursored(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    private static Post getCursored(Cursor cursor){
        Post result = new Post(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("des")),
                cursor.getString(cursor.getColumnIndex("img")),
                cursor.getInt(cursor.getColumnIndex("seller")),
                new Date(cursor.getLong(cursor.getColumnIndex("publish"))),
                cursor.getString(cursor.getColumnIndex("abs")),
                cursor.getInt(cursor.getColumnIndex("price"))
        );
        result.type = cursor.getInt(cursor.getColumnIndex("type"));
        result.school = cursor.getString(cursor.getColumnIndex("school"));
        return result;
    }

    public static void store(ArrayList<Post> added){
        DatabaseHelper.db.beginTransaction();
        for(Post item : added){
            Cursor cursor = DatabaseHelper.db.rawQuery("select * from post where id = ?;", new String[]{"" + item.id});
            cursor.moveToFirst();
            if(cursor.isAfterLast())
                DatabaseHelper.db.insert(tableName, null, getCv(item));
            cursor.close();
        }
        DatabaseHelper.db.setTransactionSuccessful();
        DatabaseHelper.db.endTransaction();
    }

    public static ContentValues getCv(Post item){
        ContentValues cv = new ContentValues();
        cv.put("id", item.id);
        cv.put("des", item.des);
        cv.put("img", item.img);
        cv.put("seller", item.seller);
        cv.put("publish", item.publish.getTime());
        cv.put("abs", item.abs);
        cv.put("price", item.price);
        cv.put("type", item.type);
        cv.put("school", item.school);
        return cv;
    }



}
