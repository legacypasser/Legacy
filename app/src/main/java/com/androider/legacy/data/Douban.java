package com.androider.legacy.data;

import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Think on 2015/5/28.
 */
public class Douban {
    public static String url = "http://api.douban.com/v2/book/isbn/:";
    public static String suf = "?fields=title,image,summary";
    public String name;
    public String img;
    public String isbn;
    public String des;
    public int price;
    public Douban(String isbn){
        this.isbn = isbn;
    }

    public void fill(){
        String result = LegacyClient.getInstance().thirdGet(url + isbn + suf);
        try {
            JSONObject obj = new JSONObject(result);
            img = obj.getString("image");
            name = obj.getString("title");
            des = obj.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJson(){
        JSONObject one = new JSONObject();
        try {
            one.put("abs", name);
            one.put("price", price);
            one.put("des", des);
            one.put("img", img);
            one.put("type", Post.doubanType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return one;
    }
}
