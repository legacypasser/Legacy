package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetService extends IntentService {

    public NetService() {
        super("NetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(Constants.intentType, -1);
        Messenger messenger = new Messenger(MainActivity.netHandler);
        Message msg = Message.obtain();
        try {
        switch (type){
            case Constants.recommendAdded:
                int pageNum = intent.getIntExtra(Constants.page, 1);
                PostConverter.stringToList(LegacyClient.getInstance().getRecommend(pageNum));
                Post.store();
                msg.what = Constants.recommendAdded;
                break;
            case Constants.detailRequest:
                int id = intent.getIntExtra(Constants.id, -1);
                String str = LegacyClient.getInstance().getRest(id);
                JSONObject jsonPost = new JSONObject(str);
                Post certain = Holder.detailed.get(id);
                certain.des = jsonPost.getString("des");
                certain.seller = jsonPost.getInt("seller");
                ContentValues cv = new ContentValues();
                cv.put("des", certain.des);
                cv.put("seller", certain.seller);
                MainActivity.db.update(Post.tableName, cv, "id = ?", new String[]{"" + id});
                String nickname = jsonPost.getString("nickname");
                Holder.peers.put(certain.seller, nickname);
                msg.what = Constants.detailRequest;
                break;
            case Constants.registrationSent:
                String regiResult = LegacyClient.getInstance().register();
                User.id = Integer.parseInt(regiResult);
                User.store();
                msg.what = Constants.registrationSent;
                break;
            case Constants.loginAttempt:
                boolean loginResult = LegacyClient.getInstance().login();
                if(loginResult)
                msg.what = Constants.loginAttempt;
                break;
        }
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
