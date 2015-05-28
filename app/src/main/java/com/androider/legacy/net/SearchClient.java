package com.androider.legacy.net;

import android.content.Intent;
import android.util.Log;

import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
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
    private static String version = "v2";
    private static String signatureVersion = "1.0";
    private static String signatureMethod = "HMAC-SHA1";
    private static ArrayList<String> searchParamList;
    private static ArrayList<String> uploadParamList;
    private static final String PUSH = "push";
    private static final String POST = "POST";
    private static final String GET = "GET";

    private static final String VERSION = "Version";
    private static final String ACCESSKEYID = "AccessKeyId";
    private static final String SIGNATUREMETHOD = "SignatureMethod";
    private static final String SIGNATUREVERSION = "SignatureVersion";
    private static final String SIGNATURENONCE = "SignatureNonce";
    private static final String TIMESTAMP = "Timestamp";
    private static final String QUERY = "query";
    private static final String INDEXNAME = "index_name";
    private static final String FETCHFIELDS = "fetch_fields";
    private static final String SIGNATURE = "Signature";

    private static final String ACTION = "action";
    private static final String TABLENAME = "table_name";
    private static Date conductDate;

    public static void initSearch(){

        searchParamList = new ArrayList<>();
        uploadParamList = new ArrayList<>();
        for(String item : getCommonList()){
            searchParamList.add(item);
            uploadParamList.add(item);
        }

        searchParamList.add(QUERY);
        searchParamList.add(INDEXNAME);
        searchParamList.add(FETCHFIELDS);

        uploadParamList.add(ACTION);
        uploadParamList.add(TABLENAME);
        uploadParamList.add("sign_mode");

        Collections.sort(searchParamList);
        Collections.sort(uploadParamList);
    }

    private static String aliDate(){
        SimpleDateFormat preFormat = new SimpleDateFormat("yyyy-MM-dd");
        preFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat sufFormat = new SimpleDateFormat("HH:mm:ss");
        sufFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        return preFormat.format(conductDate) + "T" + sufFormat.format(conductDate) + "Z";
    }

    private static ArrayList<String> getCommonList(){
        ArrayList<String> list = new ArrayList<>();
        list.add(VERSION);
        list.add(ACCESSKEYID);
        list.add(SIGNATUREMETHOD);
        list.add(SIGNATUREVERSION);
        list.add(SIGNATURENONCE);
        list.add(TIMESTAMP);
        return list;
    }
    public static String buildUploadUrl(){
        JSONObject urlObj = buildCommon();
        try {
            urlObj.put(ACTION, PUSH);
            urlObj.put(TABLENAME, tableName);
            urlObj.put("sign_mode","1");
            return serverUrl + "/index/doc/" + appName + "?" + buildParamPart(urlObj, uploadParamList,POST);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void uploadContent(Post post){
        JSONArray dataArray = new JSONArray();
        conductDate = new Date();
        try {
            JSONObject data = new JSONObject();
            data.put("id", post.id);
            data.put("des", post.des);
            data.put("abs", post.abs);
            data.put("img", post.img);
            data.put("price", post.price);
            data.put("seller", post.seller);
            data.put("publish", post.publish.getTime());
            JSONObject fullData = new JSONObject();
            fullData.put("cmd", "add");
            fullData.put("timestamp", conductDate.getTime());
            fullData.put("fields", data);
            dataArray.put(fullData);
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

    private static JSONObject buildCommon(){
        JSONObject obj = new JSONObject();
        try {
            obj.put(VERSION, version);
            obj.put(ACCESSKEYID, accessKey);
            obj.put(SIGNATUREMETHOD, signatureMethod);
            obj.put(SIGNATUREVERSION, signatureVersion);
            obj.put(SIGNATURENONCE, "" + conductDate.getTime() + (new Random().nextInt(9999)%8999 + 1000));
            obj.put(TIMESTAMP, "" + aliDate());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    private static String buildParamPart(JSONObject obj, ArrayList<String > paramlist, String method){
        String result = getEncodedPair(paramlist.get(0), obj);
        for(int i = 1; i < paramlist.size(); i++){
            result += "&" + getEncodedPair(paramlist.get(i), obj);
        }
        String signature = null;
        try {
            String toSign = POST + "&" + URLEncoder.encode("/", "utf-8") + "&" + URLEncoder.encode(result, "utf-8");
            String hmac = Encryption.hmac(toSign, bigali);
            signature = URLEncoder.encode(hmac, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result + "&" + SIGNATURE + "=" + signature;
    }

    private static String buildQueryUrl(String keyword, int start, int number){
        String queryPart = "config=start:" + start + ",hit:" + number + ",format:json&&query=default:\'"+ keyword + "\'&&sort=-publish;-RANK";
        JSONObject queryObj = buildCommon();
        try {
            queryObj.put(QUERY, queryPart);
            queryObj.put(INDEXNAME, appName);
            queryObj.put(FETCHFIELDS, "id;img;seller;publish;abs;price");
            return serverUrl + "/search?" + buildParamPart(queryObj, searchParamList, GET);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getEncodedPair(String paramName, JSONObject obj){
        try {
            return URLEncoder.encode(paramName, "utf-8") + "=" + URLEncoder.encode(obj.getString(paramName),"utf-8");
        } catch (UnsupportedEncodingException|JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Post> search(String keyWord){
        Map<String, Object> opts = new HashMap();
        opts.put("host", serverUrl);
        CloudsearchClient client = new CloudsearchClient(accessKey, bigali , opts, KeyTypeEnum.ALIYUN);
        CloudsearchSearch search = new CloudsearchSearch(client);
        search.addIndex(appName);
        search.setQueryString("default:" + "\'" + keyWord + "\'");
        search.setFormat("json");
        search.addSort("publish", "-");
        try {
            String result = search.search();
            Log.v("panbo", result);
            JSONArray itemArray = new JSONObject(result).getJSONObject("result").getJSONArray("items");
            return PostConverter.stringToList(itemArray.toString());
        } catch (IOException|JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
