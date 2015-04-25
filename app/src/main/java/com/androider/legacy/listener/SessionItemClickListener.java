package com.androider.legacy.listener;

import android.content.Intent;
import android.view.View;

import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.activity.MainActivity;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.model.Card;

/**
 * Created by Think on 2015/4/25.
 */
public class SessionItemClickListener implements OnButtonPressListener {
    private int id;
    public SessionItemClickListener(int id){
        this.id = id;
    }
    @Override
    public void onButtonPressedListener(View view, Card card) {
        Intent intent = new Intent(MainActivity.instance, ChatActivity.class);
        intent.putExtra("talker", id);
        MainActivity.instance.startActivity(intent);
    }
}
