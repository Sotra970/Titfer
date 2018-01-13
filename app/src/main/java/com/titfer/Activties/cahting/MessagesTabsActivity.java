package com.titfer.Activties.cahting;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;

import com.titfer.Fragments.Chat.MessegesTabFragment;
import com.titfer.Fragments.Chat.ProdMessegesTabFragment;
import com.titfer.R;
import com.titfer.adapter.Title_Pager_Adapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MessagesTabsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_tabs);
        ButterKnife.bind(this) ;
        setupViewPager() ;
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


        viewrPageAdapter = new Title_Pager_Adapter(getSupportFragmentManager());

        viewrPageAdapter.addFragment(new MessegesTabFragment(), "Users");

        viewrPageAdapter.addFragment(new ProdMessegesTabFragment(), "Cart");
//
//        SearchHostFragment searchFragment = new SearchHostFragment();
//        viewrPageAdapter.addFragment(searchFragment);






        viewPager.setAdapter(viewrPageAdapter);
        mtabLayout.setupWithViewPager(viewPager);



    }


    @OnClick(R.id.bar_back)
    void back(){
        onBackPressed();
    }


       @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

}
