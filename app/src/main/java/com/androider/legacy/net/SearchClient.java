package com.androider.legacy.net;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.androider.legacy.data.Post;
import com.androider.legacy.util.Encryption;
import com.opensearch.javasdk.CloudsearchClient;
import com.opensearch.javasdk.CloudsearchDoc;
import com.opensearch.javasdk.CloudsearchSearch;
import com.opensearch.javasdk.object.KeyTypeEnum;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.SimpleFormatter;

/**
 * Created by Think on 2015/5/23.
 */
public class SearchClient {
    private static String accessKey = "zsblzfu3ZoR2dziS";
    private static String bigali = "D7CFmTmNiMxs8z05zhDMyHWkQMTq81";
    private static String tableName = "legacy";
    private static String appName = "legacy";
    private static String serverUrl = "http://opensearch-cn-hangzhou.aliyuncs.com";

    public static void uploadContent(ArrayList<Post> posts){
        JSONArray dataArray = new JSONArray();
        Date conductDate = new Date();
        try {
            for(Post post : posts){
                JSONObject fullData = new JSONObject();
                fullData.put("cmd", "add");
                fullData.put("timestamp", conductDate.getTime());
                fullData.put("fields", post.toAliJson());
                dataArray.put(fullData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, Object> opts = new HashMap();
        opts.put("host", serverUrl);
        CloudsearchClient client = new CloudsearchClient(accessKey, bigali , opts, KeyTypeEnum.ALIYUN);
        CloudsearchDoc doc = new CloudsearchDoc(appName,client);
        try {
            String result = doc.push(dataArray.toString(), tableName);
            Log.v("panbo", result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void search(String keyWord, LegacyTask.RequestCallback callback){
        Map<String, Object> opts = new HashMap();
        opts.put("host", serverUrl);
        CloudsearchClient client = new CloudsearchClient(accessKey, bigali , opts, KeyTypeEnum.ALIYUN);
        CloudsearchSearch search = new CloudsearchSearch(client);
        search.addIndex(appName);
        search.setQueryString("default:" + "\'" + keyWord + "\'");
        search.setFormat("json");
        search.addSort("publish", "-");
        SearchTask task = new SearchTask(callback);
        task.execute(search);
    }

    public static ArrayList<Post> formSearchStr(String result){
        JSONArray itemArray = null;
        try {
            itemArray = new JSONObject(result).getJSONObject("result").getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Post.stringToList(itemArray.toString());
    }

    private static class SearchTask extends AsyncTask<CloudsearchSearch, Integer, String>{
        LegacyTask.RequestCallback callback;
        public SearchTask(LegacyTask.RequestCallback callback) {
            super();
            this.callback = callback;
        }

        @Override
        protected void onPostExecute(String s) {
            callback.onRequestDone(s);
        }

        @Override
        protected String doInBackground(CloudsearchSearch... params) {
            try {
                return params[0].search();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
