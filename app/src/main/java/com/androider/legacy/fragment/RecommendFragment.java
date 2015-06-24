package com.androider.legacy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.DetailActivity;
import com.androider.legacy.adapter.IndexAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.net.LegacyTask;


import java.util.ArrayList;


public class RecommendFragment extends Fragment implements IndexAdapter.BottomListener, View.OnClickListener{

    private IndexAdapter adapter;
    public static RecommendFragment instance;
    public int currentPage = 0;
    RecyclerView selfList;
    SwipeRefreshLayout holder;
    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RecommendFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_static_load, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.index_list);
        selfList.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new IndexAdapter(this, this);
        selfList.setAdapter(adapter);
        holder = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_frame);
        holder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });
        request();
        return rootView;
    }

    private String getRecommendUrl(int id, int pageNum){
        return Constants.requestPath + Constants.recommend + Constants.ask + Constants.id + id  + "&" + Constants.page + pageNum;
    }

    private void request(){
        currentPage++;
        String url = getRecommendUrl(User.instance.id, currentPage);
        LegacyClient.getInstance().callTask(url, new LegacyTask.RequestCallback() {
            @Override
            public void onRequestDone(String result) {
                ArrayList<Post> list = Post.stringToList(result);
                Post.store(list);
                for (Post item : list) {
                    adapter.addData(item);
                }
                holder.setRefreshing(false);
            }
        });
    }


    @Override
    public void onEndReach() {
        request();
    }

    @Override
    public void onClick(View v) {
        Post one = Post.get((int)v.getTag());
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Constants.detail, one);
        getActivity().startActivity(intent);
    }
}
