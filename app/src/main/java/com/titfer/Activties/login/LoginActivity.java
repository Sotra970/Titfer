package com.titfer.Activties.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.titfer.Activties.MainActivity;
import com.titfer.Activties.signup.CustomerSignUpActivity;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.Validation;
import com.titfer.app.AppContoller;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity  implements  ConnStatesCallBack.FirebaseConnectionState ,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LoginActivity";
    // strings to store data form edit text
    String email, password;

    // firebase

    private FirebaseDatabase database;
    private DatabaseReference usersdb;




    TwitterAuthClient mTwitterAuthClient ;
    private LoginButton loginButton;
    private TwitterLoginButton TwitterloginButton;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton google_sn;

    CallbackManager mFacebookCallbackManager;
    AccessToken accessToken;
    private static final int RC_SIGN_IN = 007;

    

    // ButterKnife
    @BindView(R.id.email_input_login)
    EditText emailInput;

    @BindView(R.id.password_input_login)
    EditText passwordInput;

    @BindView(R.id.login_btn)
    Button loginBrn;

    @BindView(R.id.google_sing_in_img)
    ImageView googleImg;

    @BindView(R.id.twitter_sing_in_img)
    ImageView twitterImg;

    @BindView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginButton;

    @BindView(R.id.fb_login_button)
    LoginButton fbLoginButton;

    @OnClick(R.id.login_btn)
    public void login(View view) {
        if (Validation.isEditTextEmpty(emailInput))
            return;
        email = emailInput.getText().toString();
        if (Validation.isEditTextEmpty(passwordInput))
            return;
        password = passwordInput.getText().toString();

        showloading(true);


        validatePhone(email, new OnValidateInterface() {
            @Override
            public void onFinish(boolean state , UserModel userModel) {

                if (state){
                    // // TODO: 8/6/2017  save userModel
                    Log.e("passwrods" , "phone " + userModel.getPassword() +"  " +password) ;
                        if (!userModel.getPassword().equals(password)) {
                            Toast.makeText(getApplicationContext(), getString(R.string.inncorect_email_or_pass), Toast.LENGTH_LONG).show();

                        }else {
                            AppContoller.getInstance().getPrefManager().storeUser(userModel);
                            startHome();
                        }
                        showloading(false);

                        return;
                }



                validateEmail(email, new OnValidateInterface() {
                    @Override
                    public void onFinish(boolean state , UserModel userModel) {

                        if (state) {
                            Log.e("passwrods" , "emai; " + userModel.getPassword() +"  " +password) ;
                            if (!userModel.getPassword().equals(password)){
                                Toast.makeText(getApplicationContext() , getString(R.string.inncorect_email_or_pass) , Toast.LENGTH_LONG).show();

                            }else {
                                AppContoller.getInstance().getPrefManager().storeUser(userModel);
                                startHome();
                            }
                            showloading(false);

                            return;
                        }

                        Log.e("login", "Error in Login!");
                        showloading(false);
                        Toast.makeText(getApplicationContext() , getString(R.string.inncorect_email_or_pass) , Toast.LENGTH_LONG).show();


                    }
                });
            }
        });

    }

    private void startHome() {
        if (AppContoller.getInstance().getPrefManager().getUser().getType() ==2)
            FirebaseMessaging.getInstance().subscribeToTopic("admin");
        else
        FirebaseMessaging.getInstance().subscribeToTopic("user_notifications");

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


    private void showloading(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        container.setVisibility(show ? View.GONE : View.VISIBLE);
        container.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progrssView.setVisibility(show ? View.VISIBLE : View.GONE);
        progrssView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progrssView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @OnClick(R.id.google_sing_in_img)
    void signInWithGoogle() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSHA1Code();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        mFacebookCallbackManager = CallbackManager.Factory.create();



        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        social_setup();

        // initialize firebase auth

        database = FirebaseDatabase.getInstance(Constants.Ref);
        usersdb = database.getReference("Users");
        usersdb.keepSynced(false);




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









    @Override
    public void onStop() {
        super.onStop();
        ConnStatesCallBack.unregiter();

    }



    void validatePhone(String  phone , final OnValidateInterface onPhoneValidateInterface ){



        ValueEventListener valueEventListener  = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        UserModel userModel = child.getValue(UserModel.class) ;


                        Log.e("validate_phone" , userModel.getFirstName() +"  " + child.getValue().toString());
                        onPhoneValidateInterface.onFinish(true, userModel);
                        return;
                    }


                }
                else onPhoneValidateInterface.onFinish(false, null);
                current_valueEventListener = null ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error" , databaseError.toString());
                showloading(false);
            }
        } ;
        current_valueEventListener = valueEventListener ;
        usersdb.orderByChild("phone").equalTo(( phone)).addListenerForSingleValueEvent(valueEventListener);
    }

    void validateEmail(String  email , final OnValidateInterface onPhoneValidateInterface ){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        UserModel userModel = child.getValue(UserModel.class) ;


                        Log.e("validate_phone" , userModel.getFirstName() +"  " + child.getValue().toString());
                        onPhoneValidateInterface.onFinish(true, userModel);
                        return;
                    }


                }
                else onPhoneValidateInterface.onFinish(false, null);
                current_valueEventListener = null ;
                

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error" , databaseError.toString());

            }
        } ;
        current_valueEventListener = valueEventListener ;
        usersdb.orderByChild("email").equalTo(( email)).addListenerForSingleValueEvent(valueEventListener);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void connected() {

    }

    ValueEventListener current_valueEventListener ;

    ConnStatesCallBack ConnStatesCallBack = new ConnStatesCallBack() ;
    @Override
    public void disConnected() {
        if (current_valueEventListener != null) {
            usersdb.removeEventListener(current_valueEventListener);
            showloading(false);
            Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }


    interface OnValidateInterface {

        void  onFinish(boolean state, UserModel userModel) ;

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    private void social_setup() {

        TwitterloginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        google_sn = (SignInButton) findViewById(R.id.google_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @OnClick(R.id.twitter_sing_in_img)
    void twitter_on_click(){
        
        

        mTwitterAuthClient = new TwitterAuthClient();
        showloading(true);
        twitter_login();
        TwitterloginButton.performClick();
    }

    @OnClick(R.id.fb_sing_in_img)
    void facebook_on_click()
    {
        
        

        showloading(true);
        try{
            LoginManager.getInstance().logOut();
        }catch (Exception e){}
        facebook_login();
        loginButton.performClick();
    }


    @OnClick(R.id.google_sing_in_img)
    void google_on_click(){
        showloading(true);
        google_login();
    }



    public void getSHA1Code() {

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.titfer",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:",e.toString());


        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:",e.toString());
        }
    }



    private void handleSignInResult(GoogleSignInResult result) {


        Log.e(TAG, "handleSignInResult:" + result.isSuccess()+"result "+result);
        if (result.isSuccess()) {


            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.e(TAG, "display name: " + acct.getDisplayName() + acct.getId());

            String personName = acct.getDisplayName();
            String personPhotoUrl = "";
            if(acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString();

            }
            String email = acct.getEmail();
            String id = acct.getId();
            String name = acct.getDisplayName();

            Log.e(TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            UserModel userModel = new UserModel();
            userModel.setFirstName(name);
            userModel.setEmail(email);
            userModel.setProfilePic(personPhotoUrl);
            userModel.setUid(id);
            soial_login(userModel);
            signOut();

        } else {
            // Signed out, show unauthenticated UI.
            Log.e(TAG, "google sign in result :" + result.isSuccess() +" cuz" + result.getStatus() + " ");

//            showloading(false);
//            Toast.makeText(getApplicationContext() ,getString(R.string.no_conn) , Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        Log.e("data result",requestCode+"  " );
        Log.e("data result",requestCode+"  " + FacebookSdk.isFacebookRequestCode(requestCode) );

        if (FacebookSdk.isFacebookRequestCode(requestCode))
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        else   if(requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE)
            TwitterloginButton.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        else   if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else
            showloading(false);;



    }

    public void facebook_login(){
        // If the access token is available already assign it.
        accessToken = AccessToken.getCurrentAccessToken();

        List< String > permissionNeeds = Arrays.asList("user_photos", "email",
                "user_birthday", "public_profile", "AccessToken");
        loginButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Log.e(TAG , "face" + "onSuccess");

                        String accessToken = loginResult.getAccessToken()
                                .getToken();
                        Log.e("accessToken", accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {@Override
                                public void onCompleted(JSONObject object,
                                                        GraphResponse response) {

                                    Log.e("LoginActivity",
                                            response.toString());
                                    try {
                                        String id = object.getString("id");
                                        URL profile_pic = null ;
                                        try {
                                            profile_pic = new URL(
                                                    "http://graph.facebook.com/" + id + "/picture?type=large");
                                            Log.e("profile_pic",
                                                    profile_pic + "");

                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                            showloading(false);;

                                        }
                                        UserModel userModel= new UserModel();
                                        String  name = object.getString("name");
                                        try{
                                            String  email = object.getString("email");
                                            userModel.setEmail(email);
                                        }catch (Exception e){}

                                        userModel.setFirstName(name);

                                        userModel.setUid(id);
                                        if (profile_pic != null)
                                            userModel.setProfilePic(String.valueOf(profile_pic));
                                        else
                                            userModel.setProfilePic(String.valueOf(temp_url));

                                        Log.e(TAG , userModel.getUid() + userModel.getFirstName());
                                        soial_login(userModel);
//                                    gender = object.getString("gender");
//                                    birthday = object.getString("birthday");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        showloading(false);;

                                    }




                                }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields",
                                "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        showloading(false);;
                        System.out.println("onCancel");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        showloading(false);
                        System.out.println("onError");
                        Log.e("LoginActivity", exception.toString());
                        Toast.makeText(getApplicationContext() ,getString(R.string.no_conn) , Toast.LENGTH_LONG).show();

                    }

                });


    }
    //en face book
    public void twitter_login(){

        TwitterloginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;

                Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false,true);
                user.enqueue(new Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        String name = userResult.data.name;
                        long id = userResult.data.id;
                        String photo = userResult.data.profileImageUrlHttps;


                        UserModel userModelModel =  new UserModel() ;



                        try{
                            userModelModel.setUid(String.valueOf(id));
                        }catch (Exception e){}

                        try{
                            userModelModel.setFirstName(name);
                        }catch (Exception e){}


                        try{
                            userModelModel.setProfilePic(photo);
                        }catch (Exception e){}


                        try{
                            userModelModel.setEmail(userResult.data.email);
                        }catch (Exception e){}

                        soial_login(userModelModel);
                        Log.e(photo + name + id,"photo");


                    }

                    @Override
                    public void failure(TwitterException exc) {
                        Log.e("TwitterKit", "Verify Credentials Failure", exc);
                        showloading(false);;
                    }
                });
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                CookieSyncManager.createInstance(LoginActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeSessionCookie();
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
            }
            @Override
            public void failure(TwitterException exception) {
                showloading(false);;
                Log.e("TwitterKit", "Login with Twitter failure", exception);
            }
        });
    }
    //end twitter


    void google_login(){
        signIn();
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


   
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }



    @Override
    public void onStart() {
        super.onStart();
        ConnStatesCallBack.register(this);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.e(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);


        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);

                }
            });

        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
//        Log.e(TAG, "onConnectionFailed:" + connectionResult);
//         showloading(false);
//        Toast.makeText(getApplicationContext() ,getString(R.string.no_conn) , Toast.LENGTH_LONG).show();

    }



    private void soial_login(final UserModel userModel) {


        validateUid(userModel.getUid(), new OnValidateInterface() {
            @Override
            public void onFinish(boolean state , UserModel userModel1) {

                if (state) {

                        AppContoller.getInstance().getPrefManager().storeUser(userModel1);
                        startHome();

                    return;
                }else{

                   social_reg(userModel);
                }



            }
        });


    }

    String temp_url = "https://firebasestorage.googleapis.com/v0/b/titfer-ecffc.appspot.com/o/images%2Ftemp.png?alt=media&token=bba66258-987c-4b72-bca7-3d4eaaa0816a ";
    private void social_reg(final UserModel userModel) {

        validateRegPhone(userModel.getPhone() == null ? "" :userModel.getPhone() , new CustomerSignUpActivity.OnValidateInterface() {
            @Override
            public void onFinish(boolean state) {

                if (state){
                    userModel.setPhone("");
                }



                validateRegEmail(userModel.getEmail() == null ? "" :userModel.getEmail()  , new CustomerSignUpActivity.OnValidateInterface() {
                    @Override
                    public void onFinish(boolean state) {

                        if (state){
                            userModel.setEmail("");
                        }



                        // typeID = 0 for customer, typeID = 1 for profession
                        if (TextUtils.isEmpty(userModel.getProfilePic().trim())){
                            userModel.setProfilePic(temp_url);
                        }
                        userModel.setType(0);
                        DatabaseReference pushedPostRef =  usersdb.push() ;
                        userModel.setId(pushedPostRef.getKey());
                        usersdb.child(pushedPostRef.getKey()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("firebase", "Registration Done");
                                 AppContoller.getInstance().getPrefManager().storeUser(userModel);
                                startHome();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });

                    }
                });
            }
        });


    }


    void validateUid(String  uid , final OnValidateInterface onPhoneValidateInterface ){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists())
                {
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        UserModel userModel = child.getValue(UserModel.class) ;


                        Log.e("validate_uid" , userModel.getFirstName() +"  " + child.getValue().toString());
                        onPhoneValidateInterface.onFinish(true, userModel);
                        return;
                    }


                }
                else onPhoneValidateInterface.onFinish(false, null);
                current_valueEventListener = null ;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error" , databaseError.toString());

            }
        } ;
        current_valueEventListener = valueEventListener ;
        usersdb.orderByChild("uid").equalTo(( uid)).addListenerForSingleValueEvent(valueEventListener);
    }




    void validateRegPhone(String  phone , final CustomerSignUpActivity.OnValidateInterface onPhoneValidateInterface ){


        if (TextUtils.isEmpty(phone)){
            onPhoneValidateInterface.onFinish(false);
            return;
        }

        ValueEventListener valueEventListener  = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                    Log.e("validate_phone" , userModel.toString() +"  " + dataSnapshot.toString());
                    onPhoneValidateInterface.onFinish(true);
                }
                else onPhoneValidateInterface.onFinish(false);
                current_valueEventListener = null ;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error" , databaseError.toString());
                showloading(false);
            }
        } ;
        current_valueEventListener = valueEventListener ;
        usersdb.orderByChild("phone").equalTo(( phone)).addListenerForSingleValueEvent(valueEventListener);
    }

    void validateRegEmail(String  email , final CustomerSignUpActivity.OnValidateInterface onPhoneValidateInterface ){

        if (TextUtils.isEmpty(email)){
            onPhoneValidateInterface.onFinish(false);
            return;
        }
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                    Log.e("validate_phone" , userModel.toString() +"  " + dataSnapshot.toString());
                    onPhoneValidateInterface.onFinish(true);
                }
                else onPhoneValidateInterface.onFinish(false);

                current_valueEventListener = null ;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error" , databaseError.toString());

            }
        } ;
        current_valueEventListener = valueEventListener ;
        usersdb.orderByChild("email").equalTo(( email)).addListenerForSingleValueEvent(valueEventListener);
    }

}
