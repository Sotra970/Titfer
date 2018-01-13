package com.titfer.Fragments.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titfer.Fragments.Profile.ProfileFragment;
import com.titfer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeHostFragment extends Fragment {


    public HomeHostFragment() {
        // Required empty public constructor
    }

    View  res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            res_layout  = inflater.inflate(R.layout.fragment_home_host, null, false);
        }


        return  res_layout ;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState ==null){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
        fragmentTransaction.add(R.id.home_content_frame, homeFragment);
        fragmentTransaction.addToBackStack("home_main");
        fragmentTransaction.commit();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("profilefragment", "requestCode " + requestCode);

    }
}
