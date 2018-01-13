package com.titfer.Fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titfer.Fragments.Chat.MessegesTabFragment;
import com.titfer.Fragments.Chat.ProdMessegesTabFragment;
import com.titfer.Fragments.Follow4.FollowUsersListFragment;
import com.titfer.Fragments.Follow4.UsersLikesHostFragment;
import com.titfer.Fragments.Likes.LikesHostFragment;
import com.titfer.Fragments.Likes.LikesListFragment;
import com.titfer.R;
import com.titfer.adapter.Title_Pager_Adapter;
import com.titfer.intefraces.HolderListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment {
    HolderListener onToolbarItemClicked  ;

    public void setOnToolbarItemClicked(HolderListener onToolbarItemClicked) {
        this.onToolbarItemClicked = onToolbarItemClicked;
    }

    public FavFragment() {
        // Required empty public constructor
    }


    View layout_res ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (layout_res == null){
            layout_res =inflater.inflate(R.layout.fragment_fav, container, false);
            ButterKnife.bind(this , layout_res) ;
            setupViewPager();
        }
        return layout_res ;
    }



    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout mtabLayout;
    Title_Pager_Adapter viewrPageAdapter;

    private void setupViewPager( ) {
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        // Disable clip to padding
        viewPager.setPageMargin(margin_dp);
        viewPager.setOffscreenPageLimit(2);


        viewrPageAdapter = new Title_Pager_Adapter(getChildFragmentManager());

        viewrPageAdapter.addFragment(new LikesListFragment(), "favourites");

        viewrPageAdapter.addFragment(new FollowUsersListFragment(), "following");
//
//        SearchHostFragment searchFragment = new SearchHostFragment();
//        viewrPageAdapter.addFragment(searchFragment);






        viewPager.setAdapter(viewrPageAdapter);
        mtabLayout.setupWithViewPager(viewPager);



    }



}
