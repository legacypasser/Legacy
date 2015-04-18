package com.androider.legacy.data;

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
    public ArrayList<Cachable> stringToList(String str) {
        JSONTokener jsonParser = new JSONTokener(str);
        while(jsonParser.)
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
            Post post = new Post(id,des,img,seller,publish,abs);
            return post;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
