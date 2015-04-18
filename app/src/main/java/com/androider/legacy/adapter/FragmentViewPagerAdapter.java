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
    public int getCount() {
        return fragments.size();
    }

}
