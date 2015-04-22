package com.androider.legacy.listener;

import android.content.Intent;
import android.view.View;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;

/**
 * Created by Think on 2015/4/21.
 */
public class ShouldPublishListener implements View.OnClickListener{

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.instance, PublishActivity.class);
        MainActivity.instance.startActivity(intent);
    }
}
