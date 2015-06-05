package com.androider.legacy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.adapter.SearchAdapter;
import com.androider.legacy.adapter.SimpleAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.service.SearchService;
import com.androider.legacy.util.DividerDecorator;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ResultFragment extends Fragment implements View.OnClickListener{

    public static ResultFragment instance;
    static SearchAdapter adapter;
    RecyclerView selfList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.card_list);
        selfList.setLayoutManager(new LinearLayoutManager(getActivity()));
        selfList.addItemDecoration(new DividerDecorator());
        if(adapter != null){
            selfList.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("already", "yes");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public ResultFragment(){
        instance = this;
    }

    public static ResultFragment newInstance(String param1, String param2) {
        ResultFragment fragment = new ResultFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void refreshList(){
        if(Holder.resultedPost.size() != 0){
            adapter = new SearchAdapter(this);
            for(Post item : Holder.resultedPost)
                adapter.addData(item);
            selfList.setAdapter(adapter);
        }else {
            selfList.setAdapter(new SimpleAdapter(getResources().getString(R.string.empty_search)));
        }
    }

    @Override
    public void onClick(View v) {
        PostDetailFragment.currentId = (int)v.getTag();
        SearchActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
