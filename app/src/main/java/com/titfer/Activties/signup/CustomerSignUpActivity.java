package com.titfer.Activties.signup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.Validation;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CustomerSignUpActivity extends AppCompatActivity  implements ConnStatesCallBack.FirebaseConnectionState {


    // strings to store data form edit text
    String firstName, lastName, email, phone, password;

    // UserModel instance
    UserModel userModel;

    // validation
    boolean isValid = true;
    Validation validationObj;

    // firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference usersdb;
    ValueEventListener current_valueEventListener ;


    // Butter knife
//    @BindView(R.id.input_layout_first_name)
//    TextInputLayout fNameLayoutInput;
    @BindView(R.id.first_name_input)
    EditText fNameInput;

//    @BindView(R.id.input_layout_last_name)
//    TextInputLayout lNameLayoutInput;
    @BindView(R.id.last_name_input)
    EditText lNameInput;


//    @BindView(R.id.input_layout_email)
//    TextInputLayout emailLayoutInput;
    @BindView(R.id.email_input)
    EditText emailInput;

//    @BindView(R.id.input_layout_phone)
//    TextInputLayout phoneLayoutInput;
    @BindView(R.id.phone_input)
    EditText phoneInput;

//    @BindView(R.id.input_layout_password)
//    TextInputLayout passwordLayoutInput;
    @BindView(R.id.password_input)
    EditText passwordInput;

    @BindView(R.id.terms_input_check_box)
    CheckBox termsCheckBox;

    @BindView(R.id.register_btn)
    Button registerBtn;

    @OnClick(R.id.register_btn)
    public void register(View view) {

        // get data from EditText
        firstName = fNameInput.getText().toString();
        lastName = lNameInput.getText().toString();
        email = emailInput.getText().toString();
        phone = phoneInput.getText().toString();
        password = passwordInput.getText().toString();

        if (validationObj.isEditTextEmpty(fNameInput)){
            fNameInput.setError(getString(R.string.empty_field_first_name));
            return;
        }else if (!validationObj.validateName(firstName)){
            fNameInput.setError(getString(R.string.invalid_first_name));
            return;
        }else {
            fNameInput.setError(null);

        }

        if (validationObj.isEditTextEmpty(lNameInput)){
            lNameInput.setError(getString(R.string.empty_field_last_name));
            return;
        } else if (!validationObj.validateName(lastName)){
            lNameInput.setError(getString(R.string.invalid_last_name));
            return;
        }else {
            lNameInput.setError(null);
        }


        if (validationObj.isEditTextEmpty(emailInput)){
            emailInput.setError(getString(R.string.empty_field_email));
            return;
        } else if (!validationObj.validateEmail(email)){
            emailInput.setError(getString(R.string.invalid_email));
            return;
        }else {
            emailInput.setError(null);
        }

        if (validationObj.isEditTextEmpty(phoneInput)){
            phoneInput.setError(getString(R.string.empty_field_phone));
            return;
        } else if (!validationObj.validatePhone(phone)){
            phoneInput.setError(getString(R.string.invalid_phone));
            return;
        }else {
            phoneInput.setError(null);

        }

        if (validationObj.isEditTextEmpty(passwordInput)){
            passwordInput.setError(getString(R.string.empty_field_password));
            return;
        } else {
            passwordInput.setError(null);

        }

        if (!validationObj.validateCheckBox(termsCheckBox)){
            Toast.makeText(this, "Accept terms first !", Toast.LENGTH_LONG).show();
            return;
        }


            showloading(true);

            validatePhone(phone, new OnValidateInterface() {
                @Override
                public void onFinish(boolean state) {

                    if (state){
                        phoneInput.setError(getString(R.string.dublicated_phone));
                        showloading(false);
                        phoneInput.requestFocus() ;
                        return;
                    }



                    validateEmail(email, new OnValidateInterface() {
                        @Override
                        public void onFinish(boolean state) {

                            if (state){
                                emailInput.setError(getString(R.string.dublicated_email));
                                emailInput.requestFocus() ;
                                showloading(false);
                                return;
                            }



                            // typeID = 0 for customer, typeID = 1 for profession
                            userModel = new UserModel(firstName, lastName, email, phone, password, 0, "https://firebasestorage.googleapis.com/v0/b/titfer-ecffc.appspot.com/o/images%2Ftemp.png?alt=media&token=bba66258-987c-4b72-bca7-3d4eaaa0816a");
                            DatabaseReference pushedPostRef =  usersdb.push() ;
                            userModel.setId(pushedPostRef.getKey());
                            usersdb.child(pushedPostRef.getKey()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e("firebase", "Registration Done");
                                    showloading(false);
                                    Toast.makeText(getApplicationContext() , getString(R.string.regestration_success) , Toast.LENGTH_SHORT).show();
                                         onBackPressed();
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
        }
        );
    }


    // Calligraphy callback method
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_up);



        ButterKnife.bind(this);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        firebaseAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance(Constants.Ref);

        usersdb = database.getReference("Users");

        usersdb.keepSynced(false);


        validationObj = new Validation();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {}
        };

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CaviarDreams_Bold.ttf").setFontAttrId(R.attr.fontPath).build());

    }


    @Override
    public void onStart() {
        super.onStart();
        ConnStatesCallBack.register(this);
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnStatesCallBack.unregiter();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }



    void validatePhone(String  phone , final OnValidateInterface onPhoneValidateInterface ){



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

    void validateEmail(String  email , final OnValidateInterface onPhoneValidateInterface ){

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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void connected() {

    }

    ConnStatesCallBack ConnStatesCallBack = new ConnStatesCallBack() ;
    @Override
    public void disConnected() {
        if (current_valueEventListener != null) {
            usersdb.removeEventListener(current_valueEventListener);
            showloading(false);
            Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }


    public interface OnValidateInterface {

       void  onFinish(boolean state ) ;

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}


