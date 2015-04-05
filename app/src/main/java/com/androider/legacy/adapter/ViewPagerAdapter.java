package com.androider.legacy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.List;

/**
 * Created by Think on 2015/4/3.
 */
public class ViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private List<Fragment> fragments;
    private FragmentManager fragmentManager;
    private int currentIndex = 0;



    public int getCurrentIndex(){
        return currentIndex;
    }

    public ViewPagerAdapter(List<Fragment> fragments, FragmentManager fragmentManager) {
        this.fragments = fragments;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((fragments.get(position).getView()));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments.get(position);
        if(!fragment.isAdded()){
            fragmentManager.beginTransaction()
                    .add(fragment, fragment.getClass().getSimpleName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        if(fragment.getView().getParent() == null){
            container.addView(fragment.getView());
        }
        return fragment.getView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fragments.get(currentIndex).onPause();
        if(fragments.get(position).isAdded()){
            fragments.get(position).onResume();
        }
        currentIndex = position;

    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
