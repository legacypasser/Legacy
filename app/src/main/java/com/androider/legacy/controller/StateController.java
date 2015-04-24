package com.androider.legacy.controller;

import android.content.Intent;
import android.view.View;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;

/**
 * Created by Think on 2015/4/17.
 */
public class StateController {
    public static int navState = Constants.mainState;

    public static void changeState(){
        switch (navState){
            case Constants.mainState:
                MainActivity.instance.getSupportFragmentManager().popBackStack();
                MainActivity.materialMenu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
                break;
            case Constants.chatState:
                MainActivity.materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                break;
            case Constants.detailState:
                MainActivity.materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                break;
            case Constants.resultState:
                MainActivity.materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                break;
        }
    }

    public static void setToPublish(){
        MainActivity.instance.overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.instance, PublishActivity.class);
                MainActivity.instance.startActivity(intent);
            }
        });
    }
}
