package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.listener.LeftClikedListener;
import com.androider.legacy.listener.SessionItemClickListener;
import com.androider.legacy.service.NetService;
import com.dexafree.materialList.cards.BasicImageButtonsCard;
import com.dexafree.materialList.cards.BigImageButtonsCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SessionListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SessionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SessionListFragment extends BaseListFragment{

    AddFloatingActionButton pullButton;

    public static SessionListFragment instance;
    public static SessionListFragment newInstance(String param1, String param2) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SessionListFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (MaterialListView)rootView.findViewById(R.id.card_list);
        pullButton = new AddFloatingActionButton(getActivity());
        pullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPull();
            }
        });
        ((FrameLayout)rootView).addView(pullButton);
        Session.drag();
        listSessions();
        return rootView;
    }

    public void listSessions(){
        Iterator it  = Holder.talks.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry entry = (HashMap.Entry)it.next();
            BasicImageButtonsCard card = new BasicImageButtonsCard(getActivity());
            card.setTitle(((Session) entry.getValue()).nickname);
            card.setDrawable(R.drawable.ic_person_grey600_48dp);
            card.setLeftButtonText("talk with me");
            card.setRightButtonText("right");
            card.setOnLeftButtonPressedListener(new SessionItemClickListener(((Session) entry.getValue()).peer));
            card.setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                }
            });
            selfList.add(card);
        }

    }


    public void startPull(){
        Intent intent = new Intent(getActivity(), NetService.class);
        intent.putExtra(Constants.intentType, Constants.pullMsg);
        getActivity().startService(intent);
    }
}
