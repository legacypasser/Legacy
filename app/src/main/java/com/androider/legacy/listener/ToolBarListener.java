package com.androider.legacy.listener;


import android.net.NetworkInfo;
import android.view.View;

import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;

/**
 * Created by Think on 2015/4/17.
 */
public class ToolBarListener implements View.OnClickListener {

    public static ToolBarListener instance;

    @Override
    public void onClick(View v) {
        switch (StateController.navState){
            case Constants.mainState:
                break;
            case Constants.resultState:
                StateController.navState = Constants.mainState;
                StateController.changeState();
                break;
            case Constants.chatState:
                StateController.navState = Constants.mainState;
                StateController.changeState();
                break;
            case Constants.detailState:
                StateController.navState = Constants.mainState;
                StateController.changeState();
                break;
        }
    }
}
