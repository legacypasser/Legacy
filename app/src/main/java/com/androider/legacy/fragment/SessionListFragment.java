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
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.ChatActivity;
import com.androider.legacy.adapter.SessionListAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Mate;
import com.androider.legacy.data.Record;
import com.androider.legacy.data.Session;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.util.ConnectDetector;
import com.androider.legacy.util.DividerDecorator;
import com.androider.legacy.util.SoundShouter;
import com.androider.legacy.util.StoreInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public class SessionListFragment extends Fragment implements SessionListAdapter.OnItemClickListner{

    public SessionListAdapter adapter;
    public static SessionListFragment instance;
    RecyclerView selfList;
    TextView sessionCover;
    public static SessionListFragment newInstance(String param1, String param2) {
        SessionListFragment fragment = new SessionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        instance = fragment;
        return fragment;
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
        sessionCover = (TextView)rootView.findViewById(R.id.session_cover);
        Session.drag();
        listSessions();
        if(StoreInfo.validLogin() && ConnectDetector.isConnectedToNet())
            startPull();
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
        sessionCover.setVisibility(View.GONE);
    }

    public void directAdd(Session session){
        adapter.addData(session);
        sessionCover.setVisibility(View.GONE);
    }

    public void oneCome(final Record one){
        sessionCover.setVisibility(View.GONE);
        Session owner = one.getSession();
        if(owner == null)
            Mate.getPeer(one.getPeerId(), new LegacyTask.RequestCallback() {
                @Override
                public void onRequestDone(String result) {
                    Mate mate = Mate.fromString(result);
                    Session session = Session.get(mate);
                    adapter.addData(session);
                }
            });
        else
            adapter.refresh(one.getSession());
    }

    public void startPull(){
        Record.getOnline(new LegacyTask.RequestCallback() {
            @Override
            public void onRequestDone(String result) {
                final ArrayList<Record> all = Record.strToList(result);
                if(all.size() == 0)
                    return;
                String requestStr = Constants.emptyString;
                HashSet<Integer> conflictResovler = new HashSet<Integer>();
                for (int i = 0; i < all.size(); i++){
                    Record one = all.get(i);
                    one.store();
                    conflictResovler.add(one.getPeerId());
                }
                ArrayList<Session> already = new ArrayList<Session>();
                for(Integer id : conflictResovler){
                    Mate mate = Mate.getPeer(id);
                    if(mate == null){
                        if (requestStr.equals(Constants.emptyString))
                            requestStr += id;
                        else
                            requestStr += "," + id;
                    }else {
                        already.add(Session.get(mate));
                    }
                }
                for(Session item : already){
                    adapter.addData(item);
                    if(!item.draged)
                        item.dragRecords();
                }
                if(requestStr.equals(Constants.emptyString)){
                    refreshSession(all);
                }else{
                    String url = Constants.requestPath + "infos?ids=" + requestStr;
                    LegacyClient.getInstance().callTask(url, new LegacyTask.RequestCallback() {
                        @Override
                        public void onRequestDone(String result) {
                            ArrayList<Mate> more = Mate.stringToList(result);
                            ArrayList<Session> added = Session.get(more);
                            for (Session item : added)
                                adapter.addData(item);
                            for(Record item : all){
                                Session one = item.getSession();
                                one.append(item);
                            }
                            refreshSession(all);
                        }
                    });
                }
            }
        });
    }

    private void refreshSession(ArrayList<Record> all){
        sessionCover.setVisibility(View.GONE);
        HashSet<Session> affected = new HashSet<>();
        for(Record item : all){
            Session one = item.getSession();
            affected.add(one);
        }
        ArrayList<Session> sorted = new ArrayList<>();
        for(Session item : affected){
            int pos = 0;
            for (Session session : sorted) {
                if (session.getLast() < item.getLast())
                    break;
                else
                    pos++;
            }
            sorted.add(pos, item);
        }
        for (Session item: sorted)
            adapter.refresh(item);
        SoundShouter.playInfo();
    }

    public void clearSession(){
        adapter.clearData();
    }

    @Override
    public void onItemClick(int id) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra(Constants.chat, Session.get(Mate.getPeer(id)));
        getActivity().startActivity(intent);
    }

}
