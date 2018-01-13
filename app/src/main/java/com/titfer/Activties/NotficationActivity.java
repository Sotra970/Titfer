package com.titfer.Activties;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.titfer.Fragments.Notfications.AdminNotficationsListFragment;
import com.titfer.Fragments.Notfications.NotficationsListFragment;
import com.titfer.Fragments.ReporstListFragment;
import com.titfer.R;
import com.titfer.app.AppContoller;

public class NotficationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proplems2);
        if (savedInstanceState ==null){
            Fragment fragment ;
            if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2)
                fragment = new AdminNotficationsListFragment()  ;
            else  fragment = new NotficationsListFragment() ;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager() .beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,R.anim.exit_to_left);
            fragmentTransaction.add(R.id.proplems_container, fragment);
            fragmentTransaction.addToBackStack("proplems_list");
            fragmentTransaction.commit();
        }

    }
}
