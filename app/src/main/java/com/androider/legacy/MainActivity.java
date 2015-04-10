package com.androider.legacy;

import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



import com.androider.legacy.adapter.ViewPagerAdapter;
import com.androider.legacy.fragment.MyPostListFragment;
import com.androider.legacy.fragment.RecommendFragment;
import com.androider.legacy.fragment.SessionListFragment;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.extras.toolbar.MaterialMenuIconToolbar;
import com.viewpagerindicator.UnderlinePageIndicator;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private MaterialMenuIconToolbar materialMenu;
    private Toolbar overallToolBar;

    private ViewPager viewPager;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overallToolBar = (Toolbar)findViewById(R.id.overall_toolbar);
        setSupportActionBar(overallToolBar);
        overallToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialMenu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
            }
        });
        materialMenu = new MaterialMenuIconToolbar(this, Color.WHITE, MaterialMenuDrawable.Stroke.THIN) {
            @Override
            public int getToolbarViewId() {
                return R.id.overall_toolbar;
            }
        };
        materialMenu.setNeverDrawTouch(true);
        initView();
    }

    private void initView(){

        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(RecommendFragment.newInstance("",""));
        fragmentList.add(MyPostListFragment.newInstance("",""));
        fragmentList.add(SessionListFragment.newInstance("",""));
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter( fragmentList,this.getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        UnderlinePageIndicator indicator = (UnderlinePageIndicator)findViewById(R.id.pager_indicator);
        indicator.setViewPager(viewPager);
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

        return super.onOptionsItemSelected(item);
    }
}
