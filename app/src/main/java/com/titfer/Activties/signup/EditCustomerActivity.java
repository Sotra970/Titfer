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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.Validation;
import com.titfer.app.AppContoller;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditCustomerActivity extends AppCompatActivity  implements ConnStatesCallBack.FirebaseConnectionState {

    // strings to store data form edit text
    String firstName, lastName, email, phone, password ,   uid;

    // UserModel instance
    UserModel userModel;

    // validation
    Validation validationObj;
    View focus_view ;
    // firebase
    private FirebaseDatabase database;
    private DatabaseReference usersdb;



    // Butter knife
    @BindView(R.id.first_name_prof_input)
    EditText fNameInput;
    @BindView(R.id.last_name_prof_input)
    EditText lNameInput;
    @BindView(R.id.email_prof_input)
    EditText emailInput;
    @BindView(R.id.phone_prof_input)
    EditText phoneInput;
    @BindView(R.id.password_prof_input)
    EditText passwordInput;

    @BindView(R.id.register_prof_btn)
    Button registerBtn;


    @OnClick(R.id.register_prof_btn)
    public void register(View view) {

        // get data from EditText
        firstName = fNameInput.getText().toString();
        lastName = lNameInput.getText().toString();
//        username = usernameInput.getText().toString();
        email = emailInput.getText().toString();
        phone = phoneInput.getText().toString();
        password = passwordInput.getText().toString();

        if (validationObj.isEditTextEmpty(fNameInput)){
            fNameInput.setError(getString(R.string.empty_field_first_name));
            fNameInput.requestFocus() ;
            return;
        }else if (!validationObj.validateName(firstName)){
            fNameInput.setError(getString(R.string.invalid_first_name));
            fNameInput.requestFocus() ;
            return;
        }else {
            fNameInput.setError(null);
        }

        if (validationObj.isEditTextEmpty(lNameInput)){
            lNameInput.setError(getString(R.string.empty_field_last_name));
            lNameInput.requestFocus() ;
            return;
        } else if (!validationObj.validateName(lastName)){
            lNameInput.setError(getString(R.string.invalid_last_name));
            lNameInput.requestFocus() ;
            return;
        }else {
            lNameInput.setError(null);

        }


        if (validationObj.isEditTextEmpty(emailInput)){
            emailInput.setError(getString(R.string.empty_field_email));
            emailInput.requestFocus();
            return;
        } else if (!validationObj.validateEmail(email)){
            emailInput.setError(getString(R.string.invalid_email));
            emailInput.requestFocus();
            return;
        }else {
            emailInput.setError(null);
        }

        if (validationObj.isEditTextEmpty(phoneInput)){
            phoneInput.setError(getString(R.string.empty_field_phone));
            phoneInput.requestFocus() ;
            return;
        } else if (!validationObj.validatePhone(phone)){
            phoneInput.setError(getString(R.string.invalid_phone));
            phoneInput.requestFocus() ;
            return;
        }else {
            phoneInput.setError(null);

        }

        if (validationObj.isEditTextEmpty(passwordInput)){
            passwordInput.setError(getString(R.string.empty_field_password));
            passwordInput.requestFocus() ;
            return;
        } else {
            passwordInput.setError(null);

        }





        showloading(true);

        validatePhone(phone, new CustomerSignUpActivity.OnValidateInterface() {
            @Override
            public void onFinish(boolean state) {

                if (state && !AppContoller.getInstance().getPrefManager().getUser().getPhone().equals(phone)){
                    phoneInput.setError(getString(R.string.dublicated_phone));
                    showloading(false);
                    phoneInput.requestFocus() ;
                    return;
                }



                validateEmail(email, new CustomerSignUpActivity.OnValidateInterface() {
                    @Override
                    public void onFinish(boolean state) {

                        if (state && !AppContoller.getInstance().getPrefManager().getUser().getEmail().equals(email)){
                            emailInput.setError(getString(R.string.dublicated_email));
                            emailInput.requestFocus() ;
                            showloading(false);
                            return;
                        }







                        Log.d("firebase", "uid: "  + uid);
                        // typeID = 0 for customer, typeID = 1 for profession


                        userModel = new UserModel(firstName, lastName, email, phone, password,
                                0,
                                AppContoller.getInstance().getPrefManager().getUser().getProfilePic()
                                );
                        userModel.setBio(AppContoller.getInstance().getPrefManager().getUser().getBio());
                        userModel.setId(AppContoller.getInstance().getPrefManager().getUser().getId());
                        usersdb.child(userModel.getId())
                                .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("firebase", "Registration Done");
                                showloading(false);
                                Toast.makeText(getApplicationContext() , "updated" , Toast.LENGTH_SHORT).show();


                                AppContoller.getInstance().getPrefManager().storeUser(userModel);
                                onBackPressed();


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("firebase", "Registration Errorrrrr !" + e.toString());
                            }
                        });




                    }
                });
            }
        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit);
        ButterKnife.bind(this);


        database = FirebaseDatabase.getInstance(Constants.Ref);
        usersdb = database.getReference("Users");
        usersdb.keepSynced(false);

        validationObj = new Validation();

        showloading(true);

        usersdb.child(AppContoller.getInstance().getPrefManager().getUser().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class)   ;
                // get data from EditText
                fNameInput.    setText(userModel.getFirstName());
                lNameInput.     setText(userModel.getLastName());
                emailInput.     setText(userModel.getEmail());
                phoneInput.     setText(userModel.getPhone());
                passwordInput.     setText(userModel.getPassword());
                showloading(false);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CaviarDreams_Bold.ttf").setFontAttrId(R.attr.fontPath).build());


    }

    // Calligraphy callback method
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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



    void validatePhone(String  phone , final CustomerSignUpActivity.OnValidateInterface onPhoneValidateInterface ){



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

    void validateEmail(String  email , final CustomerSignUpActivity.OnValidateInterface onPhoneValidateInterface ){

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
    public void connected() {

    }

    com.titfer.app.ConnStatesCallBack ConnStatesCallBack = new ConnStatesCallBack() ;


    @Override
    public void onStart() {
        super.onStart();
        ConnStatesCallBack.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnStatesCallBack.unregiter();
    }

    ValueEventListener current_valueEventListener ;

    @Override
    public void disConnected() {
        if (current_valueEventListener != null) {
            usersdb.removeEventListener(current_valueEventListener);
            showloading(false);
            Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }



    interface OnValidateInterface {

        void  onFinish(boolean state) ;

    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
