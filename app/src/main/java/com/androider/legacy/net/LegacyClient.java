package com.androider.legacy.net;


import org.apache.http.HttpEntity;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.util.Log;
import android.util.Pair;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.RequestData;
import com.androider.legacy.data.School;
import com.androider.legacy.data.User;
import com.androider.legacy.util.ConnectDetector;
import com.androider.legacy.util.StoreInfo;
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
    private static final String session = "session";
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
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
                .addHeader("Cookie", StoreInfo.getString(session))
                .post(body)
                .build();
        Response resp = null;
        try {
            resp = client.newCall(req).execute();
            StoreInfo.setString(session, resp.header("Set-Cookie").split(";")[0]);
            StoreInfo.setLast();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String get(String url){
        if(!ConnectDetector.isConnectedToNet())
            return RequestData.fromBase(url);
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Cookie", StoreInfo.getString(session))
                .build();
        Response  resp = null;
        try {
            resp = client.newCall(req).execute();
            StoreInfo.setString(session ,resp.header("Set-Cookie").split(";")[0]);
            StoreInfo.setLast();
            String result = resp.body().string();
            RequestData.save(url, result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String thirdGet(String url){
        if(!ConnectDetector.isConnectedToNet()){
            return RequestData.fromBase(url);
        }
        Request req = new Request.Builder()
                .url(url)
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

    public String getRestUrl(int id){
        return Constants.requestPath + Constants.detail + Constants.ask + Constants.id + id;
    }

    public String getPersonalUrl(int id){
        return Constants.requestPath + Constants.detail + Constants.ask + Constants.id + id;
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

    public String register(User instance){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("email", instance.email);
            reqData.put("password", instance.password);
            reqData.put("nickname", instance.nickname);
            reqData.put("school", instance.school);
            reqData.put("major", instance.major);
            reqData.put("lati", instance.lati);
            reqData.put("longi", instance.longi);
            reqData.put("schoolid", School.schoolId(instance.school));
            reqData.put("majorid", School.majorId(instance.major));
            reqData.put("region", School.proviceId(instance.province));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post(Constants.requestPath + Constants.register, reqData.toString());
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
                .addHeader("Cookie", StoreInfo.getString(session))
                .post(body)
                .build();
        try {
            Response resp = client.newCall(req).execute();
            StoreInfo.setString(session, resp.header("Set-Cookie").split(";")[0]);
            StoreInfo.setLast();
            return resp.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBaiduLocationUrl(String apiKey){
        return Constants.baiduUrl + apiKey;
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

    public void callTask(String url, LegacyTask.RequestCallback callback){
        LegacyTask task = new LegacyTask(callback);
        task.execute(url);
    }
}
