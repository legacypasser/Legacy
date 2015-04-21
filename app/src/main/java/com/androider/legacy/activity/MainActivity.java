package com.androider.legacy.activity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.androider.legacy.R;
import com.androider.legacy.adapter.FragmentViewPagerAdapter;
import com.androider.legacy.common.database.DatabaseHelper;
import com.androider.legacy.controller.StateController;
import com.androider.legacy.data.Constants;
import com.androider.legacy.data.User;
import com.androider.legacy.fragment.LoginFragment;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.PostDetailFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.ResultListFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.androider.legacy.listener.ToolBarListener;
import com.androider.legacy.net.LegacyClient;
import com.androider.legacy.service.NetService;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.viewpagerindicator.UnderlinePageIndicator;

import org.apache.http.cookie.params.CookieSpecPNames;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static MaterialMenuIconToolbar materialMenu;
    public static Toolbar overallToolBar;

    private ViewPager viewPager;
    private List<Fragment> fragmentList;


    public static SQLiteDatabase db;

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        overallToolBar.setNavigationOnClickListener(new ToolBarListener());

        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.overall_toolbar;
            }
        };
        materialMenu.setNeverDrawTouch(true);
        db = new DatabaseHelper(this).getWritableDatabase();
        User.drag();
        autoLogin();
        initView();
    }

    private void autoLogin(){
        if(User.id != -1){
            Intent intent = new Intent(this, NetService.class);
            intent.putExtra(Constants.intentType, Constants.loginAttempt);
            startService(intent);
            Log.v("panbo", "loginTrying");
        }else{
            Log.v("panbo", "not registered");
        }
    }
    private void initView(){
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(RecommendFragment.newInstance("", ""));
        fragmentList.add(MyPostListFragment.newInstance("",""));
        fragmentList.add(SessionListFragment.newInstance("",""));
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentViewPagerAdapter pagerAdapter = new FragmentViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        UnderlinePageIndicator indicator = (UnderlinePageIndicator)findViewById(R.id.pager_indicator);
        indicator.setViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    public void switchFragment(String fragmentName){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentName);
        if(fragment == null){
            if(fragmentName.equals(LoginFragment.class.getSimpleName())){
                fragment = LoginFragment.newInstance("", "");
            }else if(fragmentName.equals(ResultListFragment.class.getSimpleName())){
                fragment = ResultListFragment.newInstance("", "");
            }else if(fragmentName.equals(PostDetailFragment.class.getSimpleName())){
                fragment = PostDetailFragment.newInstance("", "");
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_register){
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static NetHandler netHandler = new NetHandler(instance);

    static class NetHandler extends Handler{
        WeakReference<MainActivity> activityWeakReference;
        NetHandler(MainActivity mainActivity){
            activityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.recommendAdded:
                    RecommendFragment.instance.refreshList();
                break;
                case Constants.detailRequest:
                    PostDetailFragment.instance.setView();
                    break;
                case Constants.registrationSent:
                    MainActivity.instance.autoLogin();
                    break;
                case Constants.loginAttempt:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
