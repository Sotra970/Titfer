package com.titfer.Fragments.search;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titfer.Fragments.Profile.ProfileFragment;
import com.titfer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchHostFragment extends Fragment {


    public SearchHostFragment() {
        // Required empty public constructor
    }

    View  res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            res_layout  = inflater.inflate(R.layout.fragment_search_host, container, false);
        }


        return  res_layout ;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState ==null){
        SearchCatFragment searchFragment = new SearchCatFragment();
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
            fragmentTransaction.add(R.id.serch_content_frame, searchFragment);
            fragmentTransaction.addToBackStack("home_search");
            fragmentTransaction.commit();
        }
    }
}
