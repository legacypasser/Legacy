package com.androider.legacy.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.adapter.SearchAdapter;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Post;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.net.SearchClient;
import com.androider.legacy.util.DividerDecorator;
import com.androider.legacy.util.WatcherSimplifier;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{
    EditText searchInput;
    Button searchButton;
    public static SearchActivity instance;
    static SearchAdapter adapter;
    RecyclerView selfList;
    CardView resultCover;
    public static String accessKey;
    public static String bigAli;
    public static final String accessName = "com.aliyun.search.access";
    public static final String secretName = "com.aliyun.search.secret";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        setAliKey();
        setList();
        searchInput = (EditText)findViewById(R.id.search_input);
        searchButton = (Button)findViewById(R.id.search_confirm);
        searchButton.setEnabled(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
            }
        });
        searchInput.addTextChangedListener(new WatcherSimplifier() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(Constants.emptyString))
                    searchButton.setEnabled(false);
                else
                    searchButton.setEnabled(true);
            }
        });
    }

    private void setAliKey(){
        try {
            ActivityInfo info = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            accessKey = info.metaData.getString(accessName);
            bigAli = info.metaData.getString(secretName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startSearch(){
        if(searchInput.getText().toString().equals(Constants.emptyString)){
            Toast.makeText(this, "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        View view = getWindow().peekDecorView();
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        String keyword = searchInput.getText().toString();
        SearchClient.search(keyword, new LegacyTask.RequestCallback() {
            @Override
            public void onRequestDone(String result) {
                final ArrayList<Post> resultList = SearchClient.formSearchStr(result);
                Post.store(resultList);
                refreshList(resultList);
            }
        });
        searchInput.setText(Constants.emptyString);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0)
            searchInput.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setList(){
        selfList = (RecyclerView)findViewById(R.id.result_list);
        selfList.setLayoutManager(new LinearLayoutManager(this));
        selfList.addItemDecoration(new DividerDecorator());
        resultCover = (CardView)findViewById(R.id.result_cover);
        if(adapter != null){
            selfList.setAdapter(adapter);
        }
    }
    public void refreshList(ArrayList<Post> resultList){
        if(resultList.size() != 0){
            adapter = new SearchAdapter(this);
            for(Post item : resultList)
                adapter.addData(item);
            selfList.setAdapter(adapter);
            resultCover.setVisibility(View.GONE);
        }else {
            resultCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        Post one = Post.get((int)v.getTag());
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Constants.detail, one);
        startActivity(intent);
    }
}
