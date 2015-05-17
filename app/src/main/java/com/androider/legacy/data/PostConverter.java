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

    public static SimpleDateFormat formater = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static ArrayList<Post> stringToList(String str){
        ArrayList<Post> result = new ArrayList<>();
        try {
            JSONArray jsonPosts = new JSONArray(str);
            for(int i = 0;i < jsonPosts.length(); i++){
                JSONObject jsonPost = new JSONObject(jsonPosts.getString(i));
                int id = jsonPost.getInt("id");
                String img = jsonPost.getString("img");
                Date publish = formater.parse(jsonPost.getString("publish"));
                String abs = jsonPost.getString("abs");
                int price = jsonPost.getInt("price");
                int seller = jsonPost.getInt("seller");
                Post added = new Post(id, img, publish, abs, price, seller);
                result.add(added);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


}
