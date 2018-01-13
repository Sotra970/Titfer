package com.titfer.Fragments.Follow4;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titfer.Fragments.Likes.LikesListFragment;
import com.titfer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersLikesHostFragment extends Fragment {


    public UsersLikesHostFragment() {
        // Required empty public constructor
    }

    View  res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            res_layout  = inflater.inflate(R.layout.fragment_user_likes_host, container, false);

                FollowUsersListFragment likesListFragment = new FollowUsersListFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.users_likes_content_frame, likesListFragment);
                fragmentTransaction.addToBackStack("home_likes");
                fragmentTransaction.commit();
        }


        return  res_layout ;

    }



}
