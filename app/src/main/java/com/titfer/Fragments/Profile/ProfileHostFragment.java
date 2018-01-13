package com.titfer.Fragments.Profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.titfer.R;
import com.titfer.app.AppContoller;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileHostFragment extends Fragment {


    public ProfileHostFragment() {
        // Required empty public constructor
    }

    View  res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (res_layout == null){
            res_layout  = inflater.inflate(R.layout.fragment_profile_host, container, false);
        }


        return  res_layout ;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("profileHostFragment", "onActivityCreated " +"save " + savedInstanceState);
        if (savedInstanceState ==null){
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setUserModel(AppContoller.getInstance().getPrefManager().getUser());
            profileFragment.setFrom_tab("profile");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.profile_content_frame, profileFragment);
        fragmentTransaction.addToBackStack("home_profile");
        fragmentTransaction.commit();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("profilefragment", "requestCode " + requestCode);

    }
}
