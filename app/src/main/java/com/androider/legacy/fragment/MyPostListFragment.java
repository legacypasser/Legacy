package com.androider.legacy.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MyPostListFragment extends BaseListFragment implements RecyclerListAdapter.RecycleClickListener{

    public static MyPostListFragment instance;

    private RecyclerListAdapter adapter = new RecyclerListAdapter();
    public static MyPostListFragment newInstance(String param1, String param2) {
        MyPostListFragment fragment = new MyPostListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyPostListFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        commonSet(rootView);
        swipeHolder.setEnabled(false);
        ArrayList<Post> myList = Post.listFromBase(User.id);
        if(myList.size() != 0){
            adapter.setOnClickListener(this);
            selfList.setAdapter(adapter);
            for(Post item :myList)
                adapter.addData(item);
        }else {
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.please_pub)));
        }
        return rootView;
    }

    public void addItem(){
        adapter.addData(Holder.justPub);
        if(!(selfList.getAdapter() instanceof RecyclerListAdapter)){
            adapter.setOnClickListener(this);
            selfList.setAdapter(adapter);
        }
        Holder.justPub = null;
    }

    @Override
    public void onItemClick(int id) {
        PostDetailFragment.currentId = id;
        MainActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
