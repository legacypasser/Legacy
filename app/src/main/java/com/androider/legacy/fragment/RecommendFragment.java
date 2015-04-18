package com.androider.legacy.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.card.CardFlow;
import com.androider.legacy.card.CardView;
import com.androider.legacy.card.OnCardFlowScrollChangedListener;
import com.androider.legacy.listener.LeftClikedListener;
import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecommendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecommendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendFragment extends BaseListFragment {

    private MaterialListView selfList;

    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (MaterialListView)rootView.findViewById(R.id.card_list);
        for(int i = 0; i < 10; i++){
            BigImageButtonsCard card = new BigImageButtonsCard(getActivity());
            card.setDescription("this is the test");
            card.setTitle("test");
            card.setLeftButtonText("left");
            card.setRightButtonText("right");
            card.setDrawable(R.drawable.ic_launcher);
            card.setOnLeftButtonPressedListener(new LeftClikedListener());
            selfList.add(card);
        }
        return rootView;
    }
}
