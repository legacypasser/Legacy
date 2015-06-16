package com.androider.legacy.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androider.legacy.R;
import com.androider.legacy.activity.SearchActivity;
import com.androider.legacy.adapter.SearchAdapter;
import com.androider.legacy.data.Post;
import com.androider.legacy.util.DividerDecorator;

import java.util.ArrayList;


public class ResultFragment extends Fragment implements View.OnClickListener{

    public static ResultFragment instance;
    static SearchAdapter adapter;
    RecyclerView selfList;
    CardView resultCover;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.search_result_list, container, false);
        selfList = (RecyclerView)rootView.findViewById(R.id.result_list);
        selfList.setLayoutManager(new LinearLayoutManager(getActivity()));
        selfList.addItemDecoration(new DividerDecorator());
        resultCover = (CardView)rootView.findViewById(R.id.result_cover);
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

    public void refreshList(ArrayList<Post> resultList){
        if(resultList.size() != 0){
            adapter = new SearchAdapter(this);
            for(Post item : resultList)
                adapter.addData(item);
            selfList.setAdapter(adapter);
        }else {
            resultCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        PostDetailFragment.currentId = (int)v.getTag();
        SearchActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }
}
