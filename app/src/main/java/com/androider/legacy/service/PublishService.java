package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Douban;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;
import com.androider.legacy.database.DatabaseHelper;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.SearchClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PublishService extends IntentService {

    public static ArrayList<Post> toStore;
    public PublishService() {
        super("PublishService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int intentType = intent.getIntExtra(Constants.intentType, -1);
        Messenger messenger = new Messenger(PublishActivity.instance.netHandler);
        Message msg = Message.obtain();
        switch (intentType){
            case Constants.myPublish:
                toStore = new ArrayList<>();
                JSONArray dataArray = new JSONArray();
                ArrayList<String> paths = intent.getStringArrayListExtra(getString(R.string.img_path));
                if(paths != null){
                    String rawDes = intent.getStringExtra(getString(R.string.raw_des));
                    String rawTitle = intent.getStringExtra(getString(R.string.raw_title));
                    int rawPrice = Integer.parseInt(intent.getStringExtra(getString(R.string.raw_price)));
                    String imgStr = Constants.emptyString;
                    for(String item : paths){
                        if(item != paths.get(0))
                            imgStr += ";";
                        imgStr += item;
                    }
                    Post published = new Post(-1, rawDes, imgStr, User.instance.id, new Date(), rawTitle, rawPrice);
                    published.school = User.instance.school;
                    published.type = Post.selfType;
                    dataArray.put(published.toServerJson());
                    toStore.add(published);
                }
                for(Douban item :PublishActivity.instance.beans){
                    Post temp = new Post(-1, item.des, item.img, User.instance.id, new Date(), item.name, item.price);
                    temp.school = User.instance.school;
                    temp.type = Post.doubanType;
                    dataArray.put(temp.toServerJson());
                    toStore.add(temp);
                }
                String pubResult = LegacyClient.getInstance().publish(dataArray.toString(), paths);
                String[] ids = pubResult.split(";");
                for(int i = 0; i < ids.length; i++){
                    int id = Integer.parseInt(ids[i]);
                    toStore.get(i).id = id;
                    Holder.detailed.put(id, toStore.get(i));
                    DatabaseHelper.db.insert(Post.tableName, null, Post.getCv(toStore.get(i)));
                }
                SearchClient.uploadContent(toStore);
                break;
        }
        try {
            msg.what = intentType;
            messenger.send(msg);
            if(intentType == Constants.myPublish){
                messenger = new Messenger(MainActivity.instance.netHandler);
                msg = Message.obtain();
                msg.what = Constants.myPublish;
                messenger.send(msg);
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
