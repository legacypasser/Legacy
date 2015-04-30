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
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.net.LegacyClient;

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
public class NetService extends IntentService {

    public NetService() {
        super("NetService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int type = intent.getIntExtra(Constants.intentType, -1);
        Messenger messenger = new Messenger(MainActivity.instance.netHandler);
        Message msg = Message.obtain();
        try {
        switch (type){
            case Constants.recommendAdded:
                int pageNum = intent.getIntExtra(Constants.page, 1);
                Holder.recommendPost = PostConverter.stringToList(LegacyClient.getInstance().getRecommend(pageNum));
                Post.store(Holder.recommendPost);
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
                User.storePeer(certain.seller, nickname);
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
            case Constants.myPublish:
                String idStr = LegacyClient.getInstance().publish();
                Log.v("panbo", idStr);
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
                        Holder.publishDes
                        );
                Holder.detailed.put(newlyAddedId, published);
                ContentValues publishedCv = Post.getCv(published);
                MainActivity.db.insert(Post.tableName, null, publishedCv);
                Log.v("panbo", "stroed");
                return;
            case Constants.sendChat:
                String chatResult = LegacyClient.getInstance().chat(intent.getStringExtra("content"), ChatActivity.instance.talker);
                Record returned = Record.strToObj(chatResult);
                Record.moreCome(returned);
                return;
            case Constants.pullMsg:
                Holder.justReceived = Record.getOnline();
                msg.what = Constants.pullMsg;
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
