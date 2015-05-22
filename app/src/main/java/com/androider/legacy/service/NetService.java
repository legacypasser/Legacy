package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

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
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Sender;
import com.androider.legacy.net.UdpClient;

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
                break;
            case Constants.detailRequest:
                int id = intent.getIntExtra(Constants.id, -1);
                String str = LegacyClient.getInstance().getRest(id);
                JSONObject jsonPost = new JSONObject(str);
                Post certain = Holder.detailed.get(id);
                certain.des = jsonPost.getString("des");
                ContentValues cv = new ContentValues();
                cv.put("des", certain.des);
                MainActivity.db.update(Post.tableName, cv, "id = ?", new String[]{"" + id});
                User.getPeerNick(certain.seller);
                break;
            case Constants.registrationSent:
                String regiResult = LegacyClient.getInstance().register();
                User.id = Integer.parseInt(regiResult);
                User.store();
                break;
            case Constants.loginAttempt:
                String loginResult =  LegacyClient.getInstance().login();
                if(loginResult == null){
                    msg.arg1 = Constants.unknow_login_fail;
                }else if(loginResult.equals("email_fail")){
                    msg.arg1 = Constants.email_fail;
                }else if(loginResult.equals("password_fail")){
                    msg.arg1 = Constants.password_fail;
                }else {
                    msg.arg1 = User.resetUser(new JSONObject(loginResult));
                }
                break;
            case Constants.pullMsg:
                ArrayList<Record> records = Record.getOnline();
                if(records != null){
                    for (Record item : records)
                        item.moreCome();
                    break;
                }else {
                    return;
                }
            case Constants.baiduLocation:
                String woduResult = LegacyClient.getInstance().baiduLocation();
                JSONObject whole = new JSONObject(woduResult);
                JSONObject point = whole.getJSONObject("content").getJSONObject("point");
                User.longi = point.getDouble("x");
                User.lati = point.getDouble("y");
                User.province = whole.getJSONObject("content").getJSONObject("address_detail").getString("province");
                return;
            case Constants.byebye:
                Sender.getInstance().sendToServer(""+User.id, NetConstants.offline);
                UdpClient.getInstance().close();
                return;
        }
            msg.what = type;
            messenger.send(msg);
        } catch (RemoteException|JSONException e) {
            e.printStackTrace();
        }
    }
}
