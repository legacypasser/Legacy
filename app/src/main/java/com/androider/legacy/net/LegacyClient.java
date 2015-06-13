package com.androider.legacy.net;


import org.apache.http.HttpEntity;

import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.util.Log;
import android.util.Pair;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.RequestData;
import com.androider.legacy.data.School;
import com.androider.legacy.data.User;
import com.androider.legacy.util.ConnectDetector;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ContentHandler;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Think on 2015/4/16.
 */
public class LegacyClient{
    OkHttpClient client;
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private LegacyClient(){
        client = new OkHttpClient();
        lastSession = "";
    }
    public static LegacyClient getInstance(){
        return SingletonHoler.instance;
    }
    private String lastSession;

    private static class SingletonHoler{
        private static LegacyClient instance = new LegacyClient();
    }

    private String post(String url, String json){
        RequestBody body = RequestBody.create(JSON, json);
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Cookie", lastSession)
                .post(body)
                .build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            lastSession = resp.header("Set-Cookie").split(";")[0];
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String get(String url){
        if(!ConnectDetector.isConnectedToNet())
            return RequestData.fromBase(url);
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Cookie", lastSession)
                .build();
        Response  resp = null;
        try {
            resp = client.newCall(req).execute();
            lastSession = resp.header("Set-Cookie").split(";")[0];
            String result = resp.body().string();
            RequestData.save(url, result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String thirdGet(String url){
        Request req = new Request.Builder()
                .url(url)
                .build();
        Response  resp = null;
        try {
            resp = client.newCall(req).execute();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getRecommend(int pageNum){
        String reqUrl = Constants.requestPath + Constants.recommend + Constants.ask + Constants.page + pageNum;
        return get(reqUrl);
    }

    public String getRest(int id){
        String reqUrl = Constants.requestPath + Constants.detail + Constants.ask + Constants.id + id;
        return get(reqUrl);
    }

    public String search(String keyword){
        String reqUrl = Constants.requestPath + Constants.search + Constants.ask + Constants.keyword + keyword;
        String str = get(reqUrl);
        Log.v("panbo", str);
        return str;
    }

    public String personal(int id){
        String reqUrl = Constants.requestPath + Constants.detail + Constants.ask + Constants.id + id;
        return get(reqUrl);
    }

    public String login(){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("email", User.instance.email);
            reqData.put("password", User.instance.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post(Constants.requestPath + Constants.login, reqData.toString());
    }

    public String register(){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("email", User.instance.email);
            reqData.put("password", User.instance.password);
            reqData.put("nickname", User.instance.nickname);
            reqData.put("school", User.instance.school);
            reqData.put("major", User.instance.major);
            reqData.put("lati", User.instance.lati);
            reqData.put("longi", User.instance.longi);
            reqData.put("schoolid", School.schoolId(User.instance.school));
            reqData.put("majorid", School.majorId(User.instance.major));
            reqData.put("region", School.proviceId(User.instance.province));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post(Constants.requestPath + Constants.register, reqData.toString());
    }

    public String publish(String content, ArrayList<String> paths){
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        builder.addFormDataPart("content", content);
        if(paths != null){
            for(int i = 0; i < paths.size(); i++){
                if(i == 0)
                    builder.addFormDataPart("imgs" + (i), "s_" + paths.get(i), RequestBody.create(MEDIA_TYPE_JPEG, new File(MainActivity.filePath + "s_" + paths.get(i))));
                builder.addFormDataPart("imgs" + (i + 1), paths.get(i), RequestBody.create(MEDIA_TYPE_JPEG, new File(MainActivity.filePath + paths.get(i))));
            }
        }
        RequestBody body = builder.build();
        Request req = new Request.Builder()
                .url(Constants.requestPath + Constants.publish)
                .addHeader("Cookie", lastSession)
                .post(body)
                .build();
        try {
            Response resp = client.newCall(req).execute();
            lastSession = resp.header("Set-Cookie").split(";")[0];
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String online(){
        String reqUrl = Constants.requestPath + Constants.online;
        return get(reqUrl);
    }

    public String chat(String content, int receiver){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("content", content);
            reqData.put("receiver", receiver);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post(Constants.requestPath + Constants.chat, reqData.toString());
    }

    public String interest(int id){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post(Constants.requestPath + Constants.interest, reqData.toString());
    }

    public String info(int id){
        return get(Constants.requestPath + Constants.info + Constants.ask + Constants.id + id);
    }

    public String baiduLocation(String apiKey){
        if(!ConnectDetector.isConnectedToNet()){
            return RequestData.fromBase(Constants.baiduUrl);
        }
        Request req = new Request.Builder()
                .url(Constants.baiduUrl + apiKey)
                .build();
        Response  resp = null;
        try {
            resp = client.newCall(req).execute();
            String result = resp.body().string();
            RequestData.save(Constants.baiduUrl, result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String aliSearch(String reqUrl){
        Request req = new Request.Builder().url(reqUrl).build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String aliPush(String url, Map<String, String> content){
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        Iterator<Map.Entry<String,String>> ite = content.entrySet().iterator();
        while (ite.hasNext()){
            Map.Entry<String,String> item = ite.next();
            builder.addFormDataPart(item.getKey(), item.getValue());
        }
        Request req = new Request.Builder()
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .url(url)
                    .post(builder.build())
                    .build();

        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
