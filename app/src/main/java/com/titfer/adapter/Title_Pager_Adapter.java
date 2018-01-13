package com.titfer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by sotra on 9/7/2016.
 */
public class Title_Pager_Adapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public Title_Pager_Adapter(FragmentManager fm) {
            super(fm);
        }

    public void addFragment(Fragment fragment  , String title) {
        titles.add(title) ;
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
        return titles.get(position);
    }
}
