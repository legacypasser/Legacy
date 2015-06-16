package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
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
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.User;
import com.androider.legacy.database.DatabaseHelper;
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
            case Constants.registrationSent:
                String regiResult = LegacyClient.getInstance().register();
                if(regiResult.equals("email_used")){
                    msg.arg1 = Constants.mail_occupied;
                }else {
                    User.instance.id = Integer.parseInt(regiResult);
                    User.instance.store();
                    msg.arg1 = Constants.please_active;
                }
                break;
            case Constants.loginAttempt:
                String loginResult =  LegacyClient.getInstance().login();
                if(loginResult == null){
                    msg.arg1 = Constants.unknow_login_fail;
                }else if(loginResult.equals("email_fail")){
                    msg.arg1 = Constants.email_fail;
                }else if(loginResult.equals("password_fail")){
                    msg.arg1 = Constants.password_fail;
                }else if(loginResult.equals("not_actived")){
                    msg.arg1 = Constants.not_active;
                }else{
                    msg.arg1 = User.instance.resetUser(new JSONObject(loginResult));
                }
                break;

            case Constants.byebye:
                Sender.getInstance().sendToServer(""+User.instance.id, NetConstants.offline);
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
