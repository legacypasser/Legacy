package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.adapter.IndexAdapter;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;
import com.androider.legacy.service.NetService;

import com.androider.legacy.util.LegacyProgress;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;


public class RecommendFragment extends Fragment implements IndexAdapter.BottomListener{

    LegacyProgress loadingView;
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
        adapter = new IndexAdapter(this);
        selfList.setAdapter(adapter);
        holder = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_frame);
        holder.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                request();
            }
        });
        if(User.instance.id == -1)
            request();
        return rootView;
    }

    public void request(){
        loadingView = new LegacyProgress(getActivity());
        loadingView.show();
        more();
    }

    private void more(){
        currentPage++;
        Intent intent = new Intent(getActivity(), NetService.class);
        intent.putExtra(Constants.intentType, Constants.recommendAdded);
        intent.putExtra(Constants.page, currentPage);
        getActivity().startService(intent);
    }

    public void refreshList(){
        for(Post item : Holder.recommendPost){
            adapter.addData(item);
        }
        if(loadingView != null)
            loadingView.hide();
        holder.setRefreshing(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loadingView != null)
        loadingView.dismiss();
    }

    @Override
    public void onEndReach() {
        more();
    }
}
