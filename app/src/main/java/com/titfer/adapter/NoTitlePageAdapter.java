package com.titfer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sotraa on 4/9/2016.
 */

public  class NoTitlePageAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();


    public NoTitlePageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }


    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {

        // return null to display only the icon
        return null;
    }


}

