package com.titfer.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sotra on 8/6/2017.
 */

public class AppContoller extends  com.activeandroid.app.Application {


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String CONSUMER_KEY = "cNIda0iJSam6EGnWHZH25waqC";
    private static final String CONSUMER_SECRET = "777znnOFeUY2oHWUmgOmAxNJhJW55VdSSE9L4EnyriLeNYIUK6";

    private static AppContoller mInstance;
    private MyPreferenceManager pref;
    public static synchronized AppContoller getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(CONSUMER_KEY, CONSUMER_SECRET))
                .debug(true)
                .build();
        Twitter.initialize(config);


        mInstance = this ;
        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.ERROR);
        FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        Log.e("appcontroler " , "instatnce create ") ;
        com.activeandroid.Configuration.Builder activeandroid_config = new com.activeandroid.Configuration.Builder(this);
        activeandroid_config.addModelClasses(Room_tabel.class, Mess_tabel.class);
        ActiveAndroid.initialize(activeandroid_config.create());



    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    public MyPreferenceManager getPrefManager() {
        if (pref == null) {
            pref = new MyPreferenceManager(this);
        }

        return pref;
    }


    public static boolean hasNetwork ()
    {
        return mInstance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public static String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

}
