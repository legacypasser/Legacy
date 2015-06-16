package com.androider.legacy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.adapter.SessionListAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Session;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.DividerDecorator;


import java.util.HashMap;
import java.util.Iterator;


public class SessionListFragment extends Fragment implements SessionListAdapter.OnItemClickListner{

    public SessionListAdapter adapter;
    public static SessionListFragment instance;
    RecyclerView selfList;
    CardView sessionCover;
    public static SessionListFragment newInstance(String param1, String param2) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SessionListFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_session_list, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.session_list);
        selfList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        selfList.addItemDecoration(new DividerDecorator());
        adapter = new SessionListAdapter();
        adapter.setOnclickListener(this);
        selfList.setAdapter(adapter);
        sessionCover = (CardView)rootView.findViewById(R.id.session_cover);
        Session.drag();
        listSessions();
        return rootView;
    }

    public void listSessions(){
        if(Holder.talks.size() == 0){
            sessionCover.setVisibility(View.VISIBLE);
            return;
        }
        Iterator it  = Holder.talks.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry entry = (HashMap.Entry)it.next();
            adapter.addData((Session)entry.getValue());
        }
    }

    public void refreshSessions(Session item){
        if(item.affected){
            adapter.updateData(item);
            item.affected = false;
        }
    }

    public void addSession(Session added){
        adapter.addData(added);
    }

    public void refreshSessions(){
        Iterator it  = Holder.talks.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry entry = (HashMap.Entry)it.next();
            refreshSessions((Session)entry.getValue());
        }
    }

    public void startPull(){
        Intent intent = new Intent(getActivity(), NetService.class);
        intent.putExtra(Constants.intentType, Constants.pullMsg);
        getActivity().startService(intent);
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("talker", id);
        getActivity().startActivity(intent);
    }
}
