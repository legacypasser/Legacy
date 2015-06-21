package com.androider.legacy.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.NetConstants;
import com.androider.legacy.net.Sender;
import com.androider.legacy.net.UdpClient;

import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ChatService extends IntentService {

    public ChatService() {
        super("ChatService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int intentType = intent.getIntExtra(Constants.intentType, -1);
        if(intentType == NetConstants.heartHead)
            return;
        if(intentType == NetConstants.sendChat){
            int targetId = intent.getIntExtra(Constants.id, -1);
            Sender.getInstance().sendToPeer(targetId, intent.getStringExtra(NetConstants.content));
            return;
        }
        String[] tokens;
        Record record = null;
        switch (intentType){
            case NetConstants.feedback:
                tokens = intent.getStringExtra(NetConstants.content).split(NetConstants.regex);
                long mySent = Long.parseLong(tokens[3]);
                record = Holder.waitBack.get(mySent);
                Holder.waitBack.remove(mySent);
                break;
            case NetConstants.offline:
                tokens = intent.getStringExtra(NetConstants.content).split(NetConstants.regex);
                long shouldOffline = Long.parseLong(tokens[1]);
                record = Holder.waitBack.get(shouldOffline);
                Holder.waitBack.remove(shouldOffline);
                String chatResult = LegacyClient.getInstance().chat(record.content, record.receiver);
                record = Record.strToObj(chatResult);
                break;
            case NetConstants.message:
                tokens = intent.getStringExtra(NetConstants.content).split(NetConstants.regex);
                record = Record.fromCome(tokens);
                Sender.getInstance().feedBack(record);
                break;
        }
        record.onlineCome();
        Bundle data = new Bundle();
        data.putSerializable(Constants.chat, record);
        try {
            Message msgForChat = Message.obtain();
            msgForChat.what = intentType;
            msgForChat.setData(data);
            new Messenger(ChatActivity.netHandler).send(msgForChat);
            Message msgforMain = Message.obtain();
            msgforMain.what = Constants.msgArrive;
            msgforMain.arg1 = intentType;
            msgforMain.setData(data);
            new Messenger(MainActivity.instance.netHandler).send(msgforMain);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
