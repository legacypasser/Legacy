package com.androider.legacy.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.androider.legacy.R;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.Holder;
import com.androider.legacy.data.Post;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.ResultFragment;
import com.androider.legacy.net.LegacyTask;
import com.androider.legacy.net.SearchClient;
import com.androider.legacy.service.NetService;
import com.androider.legacy.util.WatcherSimplifier;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.ref.WeakReference;

public class SearchActivity extends AppCompatActivity {

    EditText searchInput;
    Button searchButton;
    public static SearchActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        instance = this;
        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
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
                if(s.toString().equals(""))
                    searchButton.setEnabled(false);
                else
                    searchButton.setEnabled(true);
            }
        });
        switchFragment(ResultFragment.class.getSimpleName());
    }

    private void startSearch(){
        if(searchInput.getText().toString().equals("")){
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
                instance.switchFragment(ResultFragment.class.getSimpleName());
                ResultFragment.instance.refreshList(SearchClient.formSearchStr(result));
            }
        });
        searchInput.setText("");
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void switchFragment(String fragName){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragName);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(fragName.equals(ResultFragment.class.getSimpleName())){
            if(fragment == null){
                fragment = ResultFragment.newInstance("", "");
                ft.add(R.id.search_holder, fragment, fragName);
            }else {
            }
        }else if(fragName.equals(PostDetailFragment.class.getSimpleName())){
            if(fragment == null){
                fragment = PostDetailFragment.newInstance("", "");
            }
            ft.replace(R.id.search_holder, fragment, fragName);
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
