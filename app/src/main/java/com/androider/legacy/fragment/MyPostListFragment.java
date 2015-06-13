package com.androider.legacy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.activity.PublishActivity;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SearchAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.data.User;

import com.androider.legacy.service.PublishService;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.i2p.android.ext.floatingactionbutton.AddFloatingActionButton;

import java.util.ArrayList;

public class MyPostListFragment extends Fragment implements View.OnClickListener{

    public static MyPostListFragment instance;
    private RecyclerView selfList;
    private AddFloatingActionButton overButton;

    private SearchAdapter adapter;
    public static MyPostListFragment newInstance(String param1, String param2) {
        MyPostListFragment fragment = new MyPostListFragment();
        Bundle args = new Bundle();
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
        View rootView = inflater.inflate(R.layout.fragment_self_post, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.self_cards);
        selfList.setLayoutManager(new LinearLayoutManager(getActivity()));
        overButton = (AddFloatingActionButton)rootView.findViewById(R.id.all_over_button);
        overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.instance, PublishActivity.class);
                MainActivity.instance.startActivity(intent);
            }
        });
        ArrayList<Post> myList = Post.listFromBase(User.instance.id);
        adapter = new SearchAdapter(this);
        if(myList.size() != 0){
            selfList.setAdapter(adapter);
            for(Post item :myList)
                adapter.addData(item);
        }else {
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.please_pub)));
        }
        return rootView;
    }

    public void addItem(){
        for(Post item : PublishService.toStore)
            adapter.addData(item);
        if(!(selfList.getAdapter() instanceof RecyclerListAdapter)){
            selfList.setAdapter(adapter);
        }
        PublishService.toStore = null;
    }


    @Override
    public void onClick(View v) {
        PostDetailFragment.currentId = (int)v.getTag();
        MainActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
