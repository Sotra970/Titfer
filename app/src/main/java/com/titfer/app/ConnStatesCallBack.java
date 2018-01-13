package com.titfer.app;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by sotra on 8/3/2017.
 */

public class ConnStatesCallBack {

   public  interface  FirebaseConnectionState{
      void   connected();
      void   disConnected();
    }


    final Handler ha=new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

        }
    } ;
    FirebaseConnectionState firebaseConnectionState ;


    public void register(final FirebaseConnectionState refirebaseConnectionState) {
        firebaseConnectionState = refirebaseConnectionState ;
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final  DataSnapshot snapshot) {

                try {
                    ha.removeCallbacks(runnable);
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                                boolean connected = snapshot.getValue(Boolean.class);
                                    if (connected) {
                                        Log.e("firebase" , "connected");
                                        if (firebaseConnectionState  != null)
                                            firebaseConnectionState.connected();
                                    } else {
                                        Log.e("firebase" , " not connected");
                                        if (firebaseConnectionState  != null)
                                            firebaseConnectionState.disConnected();
                                    }

                                    ha.postDelayed(this , 60000) ;



                        }
                    } ;
                    ha.post(runnable) ;

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }


    public  void unregiter(){
        firebaseConnectionState  = null ;
    }


}
