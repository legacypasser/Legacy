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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androider.legacy.R;
import com.androider.legacy.activity.MainActivity;
import com.androider.legacy.adapter.RecyclerListAdapter;
import com.androider.legacy.card.CardFlow;
import com.androider.legacy.card.CardView;
import com.androider.legacy.card.OnCardFlowScrollChangedListener;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.service.NetService;

import com.androider.legacy.util.LegacyProgress;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecommendFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecommendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecommendFragment extends BaseListFragment implements RecyclerListAdapter.RecycleClickListener{

    LegacyProgress loadingView;
    private RecyclerListAdapter adapter = new RecyclerListAdapter();
    public static RecommendFragment instance;
    public int currentPage = 0;
    public static RecommendFragment newInstance(String param1, String param2) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RecommendFragment(){
        instance = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_list, container, false);
        loadingView = new LegacyProgress(getActivity());
        commonSet(rootView);
        adapter.setOnClickListener(this);
        selfList.setAdapter(adapter);
        request();
        return rootView;
    }

    private void request(){
        loadingView.show();
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
        loadingView.hide();
    }

    @Override
    public void onItemClick(int id) {
        PostDetailFragment.currentId = id;
        MainActivity.instance.switchFragment(PostDetailFragment.class.getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loadingView.dismiss();
    }
}
