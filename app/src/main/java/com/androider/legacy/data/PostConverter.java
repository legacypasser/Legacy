package com.androider.legacy.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

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
            Date publish = Date(jsonPost.getString("publish"));
            Post post = new Post()
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
