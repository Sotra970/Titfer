package com.titfer.Fragments.Likes;


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
import com.titfer.app.AppContoller;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikesHostFragment extends Fragment {


    public LikesHostFragment() {
        // Required empty public constructor
    }

    View  res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            res_layout  = inflater.inflate(R.layout.fragment_likes_host, container, false);

                LikesListFragment likesListFragment = new LikesListFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
                fragmentTransaction.add(R.id.likes_content_frame, likesListFragment);
                fragmentTransaction.addToBackStack("home_likes");
                fragmentTransaction.commit();
        }


        return  res_layout ;

    }






}
