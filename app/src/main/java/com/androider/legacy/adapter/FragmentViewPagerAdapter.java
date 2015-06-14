package com.androider.legacy.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;

/**
 * Created by Think on 2015/4/3.
 */
public class FragmentViewPagerAdapter extends FragmentPagerAdapter  {
    private List<Fragment> fragments;

    public FragmentViewPagerAdapter(List<Fragment> fragments, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(position == 0)
            return "首页";
        else if(position == 1)
            return "我的";
        else if(position == 2)
            return "对话";
        return null;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

}
