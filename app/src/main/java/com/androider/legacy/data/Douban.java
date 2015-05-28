package com.androider.legacy.data;

import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Think on 2015/5/28.
 */
public class Douban {
    public static String url = "http://api.douban.com/v2/book/isbn/:";
    public static String suf = "?fields=title,image";
    public String name;
    public String img;
    public String isbn;
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
