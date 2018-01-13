package com.titfer.Activties;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.titfer.Fragments.ReporstListFragment;
import com.titfer.Fragments.home.HomeFragment;
import com.titfer.R;

public class ProplemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proplems2);
        if (savedInstanceState ==null){
            ReporstListFragment reporstListFragment = new ReporstListFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager() .beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
            fragmentTransaction.add(R.id.proplems_container, reporstListFragment);
            fragmentTransaction.addToBackStack("proplems_list");
            fragmentTransaction.commit();
        }

    }
}
