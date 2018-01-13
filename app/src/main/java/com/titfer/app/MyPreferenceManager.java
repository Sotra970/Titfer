package com.titfer.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.IntentCompat;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.google.firebase.iid.FirebaseInstanceId;
import com.titfer.Activties.SplashActivity;
import com.titfer.Models.UserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MyPreferenceManager {

    private String TAG = MyPreferenceManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "titfer";

    // All Shared Preferences Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_FIRST_NAME = "user_fname";
    private static final String KEY_USER_LAST_NAME = "user_lname";
    private static final String KEY_USER_BIO = "bio";
    private static final String KEY_USER_BRAND_NAME = "brand";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_PHONE = "user_phone";
    private static final String KEY_USER_PASS = "user_pass";
    private static final String KEY_USER_IMG = "user_img";
    private static final String KEY_USER_TYPE = "user_type";
    private static final String KEY_USER_COUNTRY = "country";
    private static final String KEY_CURRENT_USER = "current_user";


    // Constructor
    public MyPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void storeUser(UserModel userModel) {
        editor.clear();
        editor.commit();

        editor.putString(KEY_USER_ID, userModel.getId());
        editor.putString(KEY_USER_FIRST_NAME, userModel.getFirstName());
        editor.putString(KEY_USER_LAST_NAME, userModel.getLastName());
        editor.putString(KEY_USER_EMAIL, userModel.getEmail());
        editor.putString(KEY_USER_PASS, userModel.getPassword());
        editor.putString(KEY_USER_IMG, userModel.getProfilePic());
        editor.putString(KEY_USER_BIO, userModel.getBio());
        editor.putString(KEY_USER_PHONE, userModel.getPhone());
        editor.putString(KEY_USER_BRAND_NAME, userModel.getBrandName());
        editor.putString(KEY_USER_COUNTRY, userModel.getCountry());
        editor.putInt(KEY_USER_TYPE, userModel.getType());
        editor.commit();


        Log.e(TAG, "UserModel is stored in shared preferences. " + userModel.getId() + userModel.getFirstName());
    }

    public UserModel getUser() {
        if (pref.getString(KEY_USER_ID, null) != null) {
            String lname, fname, email, pass, img, bio, brand, country;
            String id, phone;
            int type;

            id = pref.getString(KEY_USER_ID, null);
            fname = pref.getString(KEY_USER_FIRST_NAME, " ");
            lname = pref.getString(KEY_USER_LAST_NAME, " ");
            email = pref.getString(KEY_USER_EMAIL, " ");
            pass = pref.getString(KEY_USER_PASS, " ");
            img = pref.getString(KEY_USER_IMG, " ");
            brand = pref.getString(KEY_USER_BRAND_NAME, " ");
            phone = pref.getString(KEY_USER_PHONE, " ");
            country = pref.getString(KEY_USER_COUNTRY, " ");
            bio = pref.getString(KEY_USER_BIO, " ");
            type = pref.getInt(KEY_USER_TYPE, -1);


            UserModel userModelModel = new UserModel();
            userModelModel.setFirstName(fname);
            userModelModel.setLastName(lname);
            userModelModel.setId(id);
            userModelModel.setEmail(email);
            userModelModel.setPassword(pass);
            userModelModel.setProfilePic(img);
            userModelModel.setPhone(phone);
            userModelModel.setType(type);
            userModelModel.setBio(bio);
            userModelModel.setCountry(country);
            userModelModel.setBrandName(brand);
            return userModelModel;
        }
        return null;
    }

    public String get_current_user() {
        return pref.getString(KEY_CURRENT_USER, "0");

    }

    public void set_current_user(String room_id) {
        editor.putString(KEY_CURRENT_USER, room_id);
        editor.commit();
    }


    public void clear() {
        editor.clear();
        editor.commit();

        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("logout", "firex " + e.toString());
        }

        SQLiteDatabase db = ActiveAndroid.getDatabase();
        List<String> tables = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM sqlite_master WHERE type='table';", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String tableName = cursor.getString(1);
            if (!tableName.equals("android_metadata") &&
                    !tableName.equals("sqlite_sequence")) {
                tables.add(tableName);
            }
            cursor.moveToNext();
        }
        cursor.close();
        for (String tableName : tables) {
            db.execSQL("DELETE FROM " + tableName);
        }


        Intent intent = new Intent(_context, SplashActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        _context.startActivity(mainIntent);
    }


    public static final String KEY_INCREMENT_NOTFICATiON = "KEY_INCREMENT_NOTFICATiON";

    public int get_notfication() {
        int prev = pref.getInt(KEY_INCREMENT_NOTFICATiON, 0);
        return prev;
    }


    public void INCREMENT_NOTFICATiON() {
        int prev = pref.getInt(KEY_INCREMENT_NOTFICATiON, 0);
        prev++;
        editor.putInt(KEY_INCREMENT_NOTFICATiON, prev);
        editor.commit();
    }

    public void CLEAR_NOTFICATiON() {
        editor.putInt(KEY_INCREMENT_NOTFICATiON, 0);
        editor.commit();
    }
}
