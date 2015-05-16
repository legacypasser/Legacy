package com.androider.legacy.net;


import org.apache.http.HttpEntity;

import android.provider.Telephony;
import android.util.Log;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.User;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    public String get(String url){
        Request req = new Request.Builder()
                .url(url)
                .addHeader("Cookie", lastSession)
                .build();
        Response  resp = null;
        try {
            resp = client.newCall(req).execute();
            lastSession = resp.header("Set-Cookie").split(";")[0];
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

    public boolean login(){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("email", User.email);
            reqData.put("password", User.password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = post(Constants.requestPath + Constants.login, reqData.toString());
        if(result == null ||result.equals("email_fail") || result.equals("password_fail"))
            return false;
        try {

            User.resetUser(new JSONObject(result));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String register(){
        JSONObject reqData = new JSONObject();
        try {
            reqData.put("email", User.email);
            reqData.put("password", User.password);
            reqData.put("nickname", User.nickname);
            reqData.put("school", User.school);
            reqData.put("major", User.major);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = post(Constants.requestPath + Constants.register, reqData.toString());
        return result;
    }

    public String publish(){
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        builder.addFormDataPart("des", Holder.publishDes);
        builder.addFormDataPart("price", Holder.price);
        int temp = 0;
        for(String path: PublishActivity.instance.paths){
            builder.addFormDataPart("imgs" + temp, path, RequestBody.create(MEDIA_TYPE_JPEG, new File(MainActivity.filePath + path)));
            temp++;
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
        String result = get(reqUrl);
        return result;
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
}
