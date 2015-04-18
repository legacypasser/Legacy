package com.androider.legacy.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.dexafree.materialList.cards.BigImageCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.view.MaterialListView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPostListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyPostListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPostListFragment extends BaseListFragment {

    private MaterialListView selfList;

    public static MyPostListFragment newInstance(String param1, String param2) {
        MyPostListFragment fragment = new MyPostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (MaterialListView)rootView.findViewById(R.id.card_list);
        for(int i = 0; i < 10; i++){
            BigImageCard card = new BigImageCard(getActivity());
            card.setDescription("this is the test");
            card.setTitle("test");
            card.setDrawable(R.drawable.ic_launcher);
            selfList.add(card);
        }
        return rootView;
    }

}
