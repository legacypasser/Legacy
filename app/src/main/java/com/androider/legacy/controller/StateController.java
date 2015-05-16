package com.androider.legacy.controller;

import android.content.Intent;
import android.view.View;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.data.Constants;
import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;

import java.util.Stack;

/**
 * Created by Think on 2015/4/17.
 */
public class StateController {
    public static Stack<Integer> state = new Stack<>();
    public static int getCurrent(){
        return state.peek();
    }

    public static void change(int to){
        state.push(to);
    }

    public static void goBack(){
        state.pop();
    }

}
