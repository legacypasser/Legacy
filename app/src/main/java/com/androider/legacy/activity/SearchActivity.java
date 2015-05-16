package com.androider.legacy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.ResultFragment;
import com.androider.legacy.service.NetService;
import com.androider.legacy.service.SearchService;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.ref.WeakReference;

public class SearchActivity extends SimpleActivity {

    EditText searchInput;
    ImageButton searchButton;
    public static SearchActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        setToolBar();
        searchInput = (EditText)findViewById(R.id.search_input);
        searchButton = (ImageButton)findViewById(R.id.search_confirm);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        switchFragment(ResultFragment.class.getSimpleName());
    }

    private void startSearch(){
        String keyword = searchInput.getText().toString();
        Intent intent = new Intent(this, SearchService.class);
        intent.putExtra(Constants.intentType, Constants.searchReq);
        intent.putExtra(Constants.keyword, keyword);
        this.startService(intent);
    }

    public NetHandler netHandler = new NetHandler(instance);

    private static class NetHandler extends Handler {
        WeakReference<SearchActivity> activityWeakReference;
        NetHandler(SearchActivity searchActivity){
            activityWeakReference = new WeakReference<>(searchActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.searchReq:
                    ResultFragment.instance.refreshList();
                    break;
            }
        }
    }

    public void switchFragment(String fragName){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragment == null){
            if(fragName.equals(ResultFragment.class.getSimpleName())){
                fragment = ResultFragment.newInstance("", "");
                ft.add(R.id.search_holder, fragment, fragName);
            }else if(fragName.equals(PostDetailFragment.class.getSimpleName())){
                fragment = PostDetailFragment.newInstance("", "");
                ft.replace(R.id.search_holder, fragment, fragName);
                ft.addToBackStack(null);
            }
        }else {
            ft.replace(R.id.search_holder, fragment, fragName);
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
