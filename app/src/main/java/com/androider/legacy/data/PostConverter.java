package com.androider.legacy.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Think on 2015/4/18.
 */
public class PostConverter {

    public static ArrayList<Post> arryToList(ArrayList<JSONObject> objs){
        ArrayList<Post> result = new ArrayList<>();
        try {
            for(JSONObject item : objs){
                int id = item.getInt("id");
                String img = item.getString("img");
                Date publish = new Date(item.getLong("publish"));
                String abs = item.getString("abs");
                int price = item.getInt("price");
                int seller = item.getInt("seller");
                Post added = new Post(id, img, publish, abs, price, seller);
                added.type = item.getInt("type");
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


}
