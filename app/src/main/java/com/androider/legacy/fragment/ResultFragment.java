package com.androider.legacy.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.service.SearchService;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ResultFragment extends BaseListFragment implements RecyclerListAdapter.RecycleClickListener{

    public static ResultFragment instance;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.card_list);
        selfList.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeHolder = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_frame);
        swipeHolder.setEnabled(false);
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
        if(Holder.resultedPost.size() != 0){
            RecyclerListAdapter adapter = new RecyclerListAdapter();
            adapter.setOnClickListener(this);
            for(Post item : Holder.resultedPost){
                adapter.addData(item);
            }
            selfList.setAdapter(adapter);
        }else {
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.empty_search)));
        }
    }
    @Override
    public void onItemClick(int id) {
        PostDetailFragment.currentId = id;
        SearchActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
