package com.androider.legacy.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.listener.ImageListener;
import com.androider.legacy.listener.LeftClikedListener;
import com.androider.legacy.listener.SearchClickListener;
import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.view.MaterialListView;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ResultFragment extends BaseListFragment {

    public static ResultFragment instance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (MaterialListView)rootView.findViewById(R.id.card_list);
        return rootView;
    }

    public ResultFragment(){
        instance = this;
    }

    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void refreshList(){
        for(Post item : Holder.resultedPost){
            BigImageButtonsCard card = new BigImageButtonsCard(getActivity());
            card.setDescription(item.abs);
            card.setLeftButtonText("see detail");
            card.setRightButtonText("right");
            String str = Constants.imgPath + item.img.split(";")[0];
            card.setDrawable(R.drawable.ic_launcher);
            ImageLoader.getInstance().loadImage(str, new ImageListener(card));
            card.setOnLeftButtonPressedListener(new SearchClickListener(item.id));
            selfList.add(card);
        }
    }

}
