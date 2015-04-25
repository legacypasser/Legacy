package com.androider.legacy.listener;

import android.view.View;

import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.fragment.PostDetailFragment;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.model.Card;

/**
 * Created by Think on 2015/4/25.
 */
public class SearchClickListener implements OnButtonPressListener{
    private int postId;
    public SearchClickListener(int id){
        postId = id;
    }

    @Override
    public void onButtonPressedListener(View view, Card card) {
        PostDetailFragment.currentId = postId;
        SearchActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
