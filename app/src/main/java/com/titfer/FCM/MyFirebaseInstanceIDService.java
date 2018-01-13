package com.titfer.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Sotraa on 5/27/2016.
 */
public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {
    private static final String TAG = FirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + token);

    }


}
