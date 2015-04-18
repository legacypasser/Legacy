package com.androider.legacy.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Think on 2015/4/18.
 */
public class PostConverter implements Converter{
    @Override
    public String objectToString(Cachable obj) {
        return null;
    }

    @Override
    public String listToString(ArrayList<Cachable> objs) {
        return null;
    }

    @Override
    public ArrayList<Cachable> stringToList(String str) {
        try {
            ArrayList<Cachable> result = new ArrayList<Cachable>();
            JSONArray jsonPosts = new JSONArray(str);
            for(int i = 0;i < jsonPosts.length(); i++){
                Post item = (Post)stringToObj(jsonPosts.getString(i));
                result.add(item);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Cachable stringToObj(String str) {
        try {
            JSONTokener jsonParser = new JSONTokener(str);
            JSONObject jsonPost = (JSONObject)jsonParser.nextValue();
            int id = jsonPost.getInt("id");
            String des = jsonPost.getString("des");
            String img = jsonPost.getString("img");
            int seller = jsonPost.getInt("seller");
            Date publish = DateFormat.getInstance().parse(jsonPost.getString("publish"));
            String abs = jsonPost.getString("abs");
            Post post = new Post(id, des, img, seller, publish, abs);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
