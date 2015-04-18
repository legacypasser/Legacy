package com.androider.legacy.net;


import android.app.DownloadManager;

import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Post;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Think on 2015/4/16.
 */
public class LegacyClient {
    OkHttpClient client;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private LegacyClient(){
        client = new OkHttpClient();
    }
    public static LegacyClient getInstance(){
        return SingletonHoler.instance;
    }
    private static class SingletonHoler{
        private static LegacyClient instance = new LegacyClient();
    }

    private String post(String url, String json){
        RequestBody body = RequestBody.create(JSON, json);
        Request req = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp.string();
    }

    private String get(String url){
        Request req = new Request.Builder()
                .url(url)
                .build();
        Response  resp = client.newCall(request).execute();
        return resp.body().string();
    }

    public ArrayList<Post> getRecommend(int pageNum){
        String reqUrl = Constants.requestPath + Constants.recommend + Constants.ask + Constants.page + pageNum;
        String result = get(reqUrl);

    }
}
