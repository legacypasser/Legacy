package com.androider.legacy.data;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.service.NetService;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Array;
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
    public String des;
    public String img;
    public int seller;
    public Date publish;
    public String abs;

    public static final String tableName = "post";

    public Post(int id, String img, Date publish, String abs) {
        this.id = id;
        this.des = "";
        this.img = img;
        this.seller = -1;
        this.publish = publish;
        this.abs = abs;
    }

    public Post(int id,String des, String img, int seller, Date publish, String abs) {
        this.id = id;
        this.des = des;
        this.img = img;
        this.seller = seller;
        this.publish = publish;
        this.abs = abs;
    }

    public static Post get(int id) {
        Post certain = Holder.detailed.get(id);
        if(certain == null){
            certain = detailFromBase(id);
            Holder.detailed.put(id, certain);
            if(certain.des.equals("")){
                Intent intent = new Intent(MainActivity.instance, NetService.class);
                intent.putExtra(Constants.intentType, Constants.detailRequest);
                intent.putExtra(Constants.id, id);
                MainActivity.instance.startService(intent);
            }else{
                return null;
            }
        }
        return certain;
    }


    private static Post detailFromBase(int id){
        Cursor cursor = MainActivity.db.rawQuery("select * from post where id=?;", new String[]{"" + id});
        cursor.moveToFirst();
        Post result = getCursored(cursor);
        cursor.close();
        Holder.detailed.put(id, result);
        return result;
    }

    private static ArrayList<Post> listFromBase(String url){
        ArrayList<Post> result = new ArrayList<Post>();
        int pageNum = Integer.parseInt(url.split(Constants.connnector)[1]);
        int skip = pageNum * Constants.eachPage - Constants.eachPage;
        Cursor cursor = MainActivity.db.rawQuery("select * from post limit " + Constants.eachPage + " offset " + skip, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            result.add(getCursored(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    private static Post getCursored(Cursor cursor){
        return new Post(
                cursor.getInt(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("des")),
                cursor.getString(cursor.getColumnIndex("img")),
                cursor.getInt(cursor.getColumnIndex("seller")),
                new Date(cursor.getLong(cursor.getColumnIndex("publish"))),
                cursor.getString(cursor.getColumnIndex("abs"))
        );
    }

    public static void store(){
        for(Post item : Holder.recommendPost){
            MainActivity.db.insert(tableName, null, getCv(item));
        }
    }

    private static ContentValues getCv(Post item){
        ContentValues cv = new ContentValues();
        cv.put("des", item.des);
        cv.put("img", item.img);
        cv.put("seller", item.seller);
        cv.put("publish", item.publish.getTime());
        cv.put("abs", item.abs);
        return cv;
    }


}
