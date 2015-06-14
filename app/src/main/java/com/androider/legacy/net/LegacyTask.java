package com.androider.legacy.net;

import android.os.AsyncTask;

/**
 * Created by Think on 2015/6/14.
 */
public class LegacyTask extends AsyncTask<String, Integer, String> {
    private RequestCallback callback;
    @Override
    protected String doInBackground(String... params) {
        return LegacyClient.getInstance().get(params[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        if(callback != null)
            callback.onRequestDone(s);
    }

    public LegacyTask(RequestCallback callback) {
        super();
        this.callback = callback;
    }
    public interface RequestCallback{
        void onRequestDone(String result);
    }
}