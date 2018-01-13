package com.titfer.Activties;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.titfer.Activties.Insert.WriteReportActivity;
import com.titfer.Activties.Upload.AddSpace1UploadActivity;
import com.titfer.Activties.cahting.MessagesTabsActivity;
import com.titfer.Activties.Upload.EventsUploadActivity;
import com.titfer.Activties.Upload.NewsUploadActivity;
import com.titfer.Fragments.CartListFragment;
import com.titfer.Fragments.FavFragment;
import com.titfer.Fragments.Profile.ProfileHostFragment;
import com.titfer.Fragments.search.SearchHostFragment;
import com.titfer.Fragments.home.HomeHostFragment;
import com.titfer.R;
import com.titfer.adapter.NoTitlePageAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.MyPreferenceManager;
import com.titfer.holder.TabIconHolder;
import com.yalantis.ucrop.UCrop;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity   {


    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout mtabLayout;

     @BindView(R.id.toolbar_menu)
    ImageView toolbar_menu;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    TabIconHolder  favTabIcon ,  searchTabIcon , homeTabIcon , profileTabIcon  , cartTabIcon ;
     NoTitlePageAdapter viewrPageAdapter;
    boolean exit = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this) ;
        setSupportActionBar(toolbar);
        setup_menu();
        setupViewPager();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CaviarDreamsBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onStart() {
        super.onStart();
            signInAnonymously();
    }


    private void signInAnonymously() {
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("homeActivity", "signInAnonymously:FAILURE", exception);
                    }
                });
    }



    private void setupViewPager( ) {
        int margin_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
        // Disable clip to padding
           viewPager.setPageMargin(margin_dp);
        viewPager.setOffscreenPageLimit(5);

        viewrPageAdapter = new NoTitlePageAdapter(getSupportFragmentManager());

        FavFragment favFragment = new FavFragment();
        favTabIcon = new TabIconHolder( R.drawable.favorite, Config.FAV_TAB, this);
        viewrPageAdapter.addFragment(favFragment);

        SearchHostFragment searchFragment = new SearchHostFragment();
        searchTabIcon = new TabIconHolder( R.drawable.search, Config.SEARCH_TAB, this);
        viewrPageAdapter.addFragment(searchFragment);

        HomeHostFragment homeHostFragment = new HomeHostFragment();
        homeTabIcon = new TabIconHolder( R.drawable.home, Config.HOME_TAB, this);
        viewrPageAdapter.addFragment(homeHostFragment);

        ProfileHostFragment profileFragment = new ProfileHostFragment();
        profileTabIcon = new TabIconHolder( R.drawable.profile, Config.PROFILE_TAB, this);
        viewrPageAdapter.addFragment(profileFragment);

        CartListFragment cartFragment= new CartListFragment();
        cartTabIcon = new TabIconHolder( R.drawable.shopping, Config.CART_TAB, this);
        viewrPageAdapter.addFragment(cartFragment);




        mtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               try{
                   tab.getCustomView().animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator(3));

               }catch (Exception e){}
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                try{
                    tab.getCustomView().animate().alpha(0.9f).setDuration(100).setInterpolator(new DecelerateInterpolator(2));
                }catch (Exception e){}
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager.setAdapter(viewrPageAdapter);
        mtabLayout.setupWithViewPager(viewPager);

        mtabLayout.post(new Runnable() {
            @Override
            public void run() {
                mtabLayout.getTabAt(0).setCustomView(favTabIcon.getView());
                mtabLayout.getTabAt(1).setCustomView(searchTabIcon.getView());
                mtabLayout.getTabAt(2).setCustomView(homeTabIcon.getView());
                mtabLayout.getTabAt(3).setCustomView(profileTabIcon.getView());
                mtabLayout.getTabAt(4).setCustomView(cartTabIcon.getView());
            }
        });


        viewPager.setCurrentItem(2,false);


//        TabLayout.Tab tab =   mtabLayout.getTabAt(2) ;
//        tab.getCustomView().animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator(3));
//        (tab.getCustomView()).setBackgroundColor( ContextCompat.getColor(getApplicationContext(),R.color.gray200));



    }
    PopupMenu popup;

    void setup_menu(){

        popup = new PopupMenu(this,toolbar_menu ) ;
        MenuInflater inflater = popup.getMenuInflater();
        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2 )
        inflater.inflate(R.menu.admin_toolbar_menu_actions, popup.getMenu());
        else
        inflater.inflate(R.menu.toolbar_menu_actions, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.e("item selected" ,"logout "  + (item.getItemId()== R.id.logout_action) ) ;

                switch (item.getItemId()){
                    case R.id.logout_action :
                        AppContoller.getInstance().getPrefManager().clear();
                        break;

                    case R.id.problem_action :
                        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2 )
                            start_proplem_list();
                        else
                        start_wr_proplem();
                        break;

                    case R.id.news_action :
                        start_news();
                        break;

                    case R.id.event_action :
                        start_events();
                        break;
                    case R.id.add_space_1_action :
                        start_add_uplod("AdSpace1");
                        break;
                    case R.id.add_space_2_action :
                        start_add_uplod("AdSpace2");
                        break;
                }
                return false;
            }
        });
    }

    private void start_wr_proplem() {
        Intent i = new Intent(getApplicationContext(), WriteReportActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void start_add_uplod(String space) {
        Intent i = new Intent(getApplicationContext(), AddSpace1UploadActivity.class);
        i.putExtra("space" , space) ;
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void start_proplem_list() {
        Intent i = new Intent(getApplicationContext(), ProplemsActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void start_news() {
        Intent i = new Intent(getApplicationContext(), NewsUploadActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void start_events() {
        Intent i = new Intent(getApplicationContext(), EventsUploadActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @OnClick(R.id.toolbar_menu)
    public void showPopup() {

        popup.show();
    }


    @OnClick(R.id.toolbar_chat)
    public void toolbar_chat() {

        startActivity(new Intent(getApplicationContext(), MessagesTabsActivity.class));
        overridePendingTransition(R.anim.enter_from_right , R.anim.exit_to_left);
    }




    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().contains("home"))
        {
            if (!exit) {
                Toast.makeText(getApplicationContext() ,"press back again to exit " , Toast.LENGTH_SHORT ).show();
                exit = true ;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            exit = false ;
                        }catch (Exception e){} ;
                    }
                } , 15000) ;
            }
           else if (exit){
            supportFinishAfterTransition();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

        }
        else {
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().contains("favs"))
                viewPager.setCurrentItem(0);
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().contains("search"))
                viewPager.setCurrentItem(1);
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().contains("main"))
                viewPager.setCurrentItem(2);
            if (getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName().contains("profile"))
                viewPager.setCurrentItem(3);
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("main acresult", "requestCode " + requestCode);


        if (requestCode == UCrop.REQUEST_CROP ){

             if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }else {
                 Log.e("main acresult", "resultCode " + resultCode);

                 data.setAction(Config.PROFILE_CROP) ;
                 LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(data);

             }


        }
    }



    String TAG = "profile_img_upload";
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(getApplicationContext(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.show_notfication)
    void open_ntofi(){
        AppContoller.getInstance().getPrefManager().CLEAR_NOTFICATiON();
        refresh_notfication();
        Intent i = new Intent(getApplicationContext(), NotficationActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    void refresh_notfication(){
        try {
            int cont = AppContoller.getInstance().getPrefManager().get_notfication()  ;
            if (cont == 0 )
                notfication_container.setVisibility(View.GONE);
            else{
                notfication_container.setVisibility(View.VISIBLE);
                notfication_count.setText(cont+"");
            }

        }catch (Exception e){}
    }

    @BindView(R.id.messages_notification_count)
    TextView notfication_count ;

    @BindView(R.id.messages_notification_count_con)
    View notfication_container ;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh_notfication() ;
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                broadcastReceiver,
                new IntentFilter( MyPreferenceManager.KEY_INCREMENT_NOTFICATiON));
        try {
            refresh_notfication();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
