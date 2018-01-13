package com.titfer.Activties;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;

import com.titfer.Activties.login.LoginHostActivity;
import com.titfer.R;
import com.titfer.app.AppContoller;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.logo)
    ImageView logo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this) ;

//
//        logo.setAlpha(0.2f);
//        logo.setScaleX(0.7f);
//        logo.setScaleY(0.7f);

        Animation animationSet = AnimationUtils.loadAnimation(this, R.anim.logo_anim);
        animationSet.setInterpolator(new AnticipateInterpolator(2));
        animationSet.setDuration(500);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                final Animation.AnimationListener animatorListener= this ;
                Animation animationSet = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_anim2);
                animationSet.setInterpolator(new LinearOutSlowInInterpolator());
                animationSet.setDuration(500);
                animationSet.setAnimationListener(animatorListener);
                animationSet.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Animation animationSet = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_anim3);
                        animationSet.setInterpolator(new FastOutLinearInInterpolator());
                        animationSet.setDuration(500);
                        animationSet.setAnimationListener(animatorListener);
                        logo.startAnimation(animationSet);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                logo.startAnimation(animationSet);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//        animationSet.setRepeatMode(Animation.INFINITE);
        logo.startAnimation(animationSet);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i ;
                if (AppContoller.getInstance().getPrefManager().getUser() == null)
                i= new Intent(getApplicationContext(), LoginHostActivity.class);
                else i = new Intent(getApplication() , MainActivity.class) ;
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        } , 3000) ;
    }
}
