package com.androider.legacy.listener;

import android.view.View;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.fragment.PostDetailFragment;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.model.Card;

/**
 * Created by Think on 2015/4/17.
 */
public class LeftClikedListener implements OnButtonPressListener {
    @Override
    public void onButtonPressedListener(View view, Card card) {
        MainActivity.instance.switchFragment(MainActivity.detailFragName);
        MainActivity.instance.materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
    }
}
