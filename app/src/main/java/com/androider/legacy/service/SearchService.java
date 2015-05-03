package com.androider.legacy.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.PostConverter;
import com.androider.legacy.net.LegacyClient;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SearchService extends IntentService {


    public SearchService() {
        super("SearchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int intentType = intent.getIntExtra(Constants.intentType, -1);
        Messenger messenger = new Messenger(SearchActivity.netHandler);
        Message msg = Message.obtain();
        switch (intentType){
            case Constants.searchReq:
                String keyword = intent.getStringExtra(Constants.keyword);
                Holder.resultedPost = PostConverter.stringToList(LegacyClient.getInstance().search(keyword));
                msg.what = Constants.searchReq;
                break;
        }
        try {
            messenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
