package com.androider.legacy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SessionListAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.Session;
import com.androider.legacy.data.User;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.DividerDecorator;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class SessionListFragment extends Fragment implements SessionListAdapter.OnItemClickListner{

    public SessionListAdapter adapter;
    public static SessionListFragment instance;
    RecyclerView selfList;
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
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.card_list);
        selfList.setLayoutManager(new LinearLayoutManager(getActivity()));
        selfList.addItemDecoration(new DividerDecorator());
        adapter = new SessionListAdapter();
        Session.drag();
        listSessions();
        return rootView;
    }

    public void listSessions(){
        if(Holder.talks.size() == 0){
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.empty_session)));
            return;
        }
        useSessionAdapter();
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

    public void useNewAdapter(){
        adapter = new SessionListAdapter();
        selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.empty_session)));
    }

    public void addSession(Session added){
        adapter.addData(added);
        if(!(selfList.getAdapter() instanceof SessionListAdapter))
            useSessionAdapter();
    }
    private void useSessionAdapter(){
        adapter.setOnclickListener(this);
        selfList.setAdapter(adapter);
    }
    public void refreshSessions(){
        if(!(selfList.getAdapter() instanceof SessionListAdapter)){
            useSessionAdapter();
        }
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
