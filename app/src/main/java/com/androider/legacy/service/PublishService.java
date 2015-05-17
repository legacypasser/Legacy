package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;

import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PublishService extends IntentService {

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
                String idStr = LegacyClient.getInstance().publish();
                int newlyAddedId = Integer.parseInt(idStr);
                String imgStr = "";
                for(String item : PublishActivity.instance.paths){
                    imgStr += item + ";";
                }
                Post published = new Post(newlyAddedId,
                        Holder.publishDes,
                        imgStr,
                        User.id,
                        new Date(),
                        Holder.publishDes,
                        Integer.parseInt(Holder.price)
                );
                Holder.justPub = published;
                Holder.detailed.put(newlyAddedId, published);
                ContentValues publishedCv = Post.getCv(published);
                MainActivity.db.insert(Post.tableName, null, publishedCv);
                msg.what = Constants.myPublish;
                break;
        }
        try {
            messenger.send(msg);
            messenger = new Messenger(MainActivity.instance.netHandler);
            msg = Message.obtain();
            msg.what = Constants.myPublish;
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
