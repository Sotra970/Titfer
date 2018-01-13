package com.titfer.Activties.signup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
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
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfessionalSignUpActivity extends AppCompatActivity  implements ConnStatesCallBack.FirebaseConnectionState {

    // strings to store data form edit text
    String firstName, lastName, email, phone, password, brandName, profession , country, specialty1, specialty2, specialty_search_key ,   uid;
    ArrayList<String> subCat = new ArrayList<>() ;
    int professionChecked = 0;
    int countryChecked = 0;
    boolean professionIsChecked = false;
    boolean countryIsChecked = false;

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
    @BindView(R.id.brandName_prof_input)
    EditText brandNameInput;
    @BindView(R.id.email_prof_input)
    EditText emailInput;
    @BindView(R.id.phone_prof_input)
    EditText phoneInput;
    @BindView(R.id.password_prof_input)
    EditText passwordInput;
    @BindView(R.id.profession_btn)
    TextView professionBtn;
    @BindView(R.id.county_btn)
    TextView county_btn;
    @BindView(R.id.specialty_btn)
    TextView specialtyBtn;
    @BindView(R.id.prof_terms_input_check_box)
    CheckBox termsCheckBox;
    @BindView(R.id.register_prof_btn)
    Button registerBtn;


    @OnClick(R.id.profession_btn)
    public void pickProfession(View view){

        final  String[] professionList  = getResources().getStringArray(R.array.professionList);
        final int[] selected = new int[1];
        selected[0] = professionChecked;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.profession));
        builder.setSingleChoiceItems(professionList, selected[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    profession = professionList[which];
                    System.out.println(profession);
                    professionChecked = which;
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                profession = professionList[professionChecked];
                System.out.println(profession);
                professionBtn.setText(profession);
                professionIsChecked = true;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @OnClick(R.id.county_btn)
    public void pick_country(View view){

        final  String[] countryList  = getResources().getStringArray(R.array.countryList);
        final int[] selected = new int[1];
        selected[0] = countryChecked;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.country));
        builder.setSingleChoiceItems(countryList, selected[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                country = countryList[which];
                System.out.println(country);
                countryChecked = which;
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                country = countryList[countryChecked];
                System.out.println(countryList);
                county_btn.setText(country);
                countryIsChecked = true;
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @OnClick(R.id.specialty_btn)
    public void pickSpeciality(View view){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.specialty));

        final  String[] specialtyListArr  = getResources().getStringArray(R.array.specialtyList);
        final boolean [] checkedItems = new boolean[specialtyListArr.length];

        for ( int i=0; i < specialtyListArr.length ; i++  ){
            boolean exist = false ;
           for (String cat  : subCat){
               if (cat.equals(specialtyListArr[i])) {
                   System.out.println(cat);
                   exist = true ;
                   break;
               }
           }
            checkedItems[i] = exist ;
        }

        builder.setMultiChoiceItems(specialtyListArr, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                if (isChecked){
                    if (subCat.size()<2){
                        subCat.add(specialtyListArr[which]) ;
                    }else {
                        checkedItems[which] = false ;
                        ((AlertDialog) dialog).getListView().setItemChecked(which,false);
                        Toast.makeText(ProfessionalSignUpActivity.this,getString(R.string.at_most_2),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    subCat.remove(specialtyListArr[which]) ;
                }
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (subCat.size()==2){
                specialtyBtn.setText(subCat.get(0)+" , "+subCat.get(1));
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                subCat.clear();
                specialtyBtn.setText("");

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @OnClick(R.id.register_prof_btn)
    public void register(View view) {

        // get data from EditText
        firstName = fNameInput.getText().toString();
        lastName = lNameInput.getText().toString();
        brandName = brandNameInput.getText().toString();
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

//        if (validationObj.isEditTextEmpty(usernameInput)){
//            usernameInput.setError(getString(R.string.empty_field_username));
//            usernameInput.requestFocus() ;
//            return;
//        }else {
//            usernameInput.setError(null);
//        }

        if (validationObj.isEditTextEmpty(brandNameInput)){
            brandNameInput.setError(getString(R.string.empty_field_brand_name));
            brandNameInput.requestFocus() ;
            return;
        }else {
            brandNameInput.setError(null);
        }

        if (!countryIsChecked){

            county_btn.setError(getString(R.string.empty_chicked_country));
            Toast.makeText(ProfessionalSignUpActivity.this,getString(R.string.empty_chicked_country),Toast.LENGTH_SHORT).show();
            return;
        }else {
            county_btn.setError(null);
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
        }else {
            passwordInput.setError(null);

        }

        if (!validationObj.validateCheckBox(termsCheckBox)){
            Toast.makeText(this, "Accept terms first !", Toast.LENGTH_LONG).show();
            termsCheckBox.requestFocus() ;
            return;
        }
        if (!professionIsChecked){

            professionBtn.setError(getString(R.string.empty_chicked_proffession));
            Toast.makeText(ProfessionalSignUpActivity.this,getString(R.string.empty_chicked_proffession),Toast.LENGTH_SHORT).show();
            return;
        }else {
            professionBtn.setError(null);
        }
        if (subCat.size() != 2  && !profession.equals(getString(R.string.Hairdressers)) ){
            professionBtn.setError(getString(R.string.at_least_2));
            Toast.makeText(ProfessionalSignUpActivity.this,getString(R.string.at_least_2),Toast.LENGTH_SHORT).show();
            return;
        }else {
            specialty1 = subCat.get(0);
            specialty2 = subCat.get(1);
            specialty_search_key = specialty1+"_"+specialty2 ;
            professionBtn.setError(null);
        }




        showloading(true);


        validateBrand(brandName, new ProfessionalSignUpActivity.OnValidateInterface() {
            @Override
            public void onFinish(boolean state) {

                if (state){
                    brandNameInput.setError(getString(R.string.dublicated_brand));
                    showloading(false);
                    brandNameInput.requestFocus() ;
                    return;
                }

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







                                Log.d("firebase", "uid: "  + uid);
                                // typeID = 0 for customer, typeID = 1 for profession
                                userModel = new UserModel(firstName, lastName, email, phone, password, brandName, profession, specialty1, specialty2 , specialty_search_key, 1, "https://firebasestorage.googleapis.com/v0/b/titfer-ecffc.appspot.com/o/images%2Ftemp.png?alt=media&token=bba66258-987c-4b72-bca7-3d4eaaa0816a");
                                userModel.setCountry(country);
                                final  DatabaseReference pushedPostRef =  usersdb.push() ;
                                userModel.setId(pushedPostRef.getKey());
                                usersdb.child(pushedPostRef.getKey()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e("firebase", "Registration Done");
                                        showloading(false);
                                        Toast.makeText(getApplicationContext() , getString(R.string.regestration_success) , Toast.LENGTH_SHORT).show();
                                        onBackPressed();


                                        DatabaseReference sp1_ref    = database.getReference(specialty1) ;
                                        sp1_ref.child(pushedPostRef.getKey()).setValue(true) ;

                                        DatabaseReference sp2_ref    = database.getReference(specialty2) ;
                                        sp2_ref.child(pushedPostRef.getKey()).setValue(true) ;

                                        if (userModel.getProfession().equals(getString(R.string.Hairdressers))){
                                            DatabaseReference sp3_ref    = database.getReference(userModel.getProfession()) ;
                                            sp3_ref.child(pushedPostRef.getKey()).setValue(true) ;
                                        }


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
                }) ;
            }
        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_sign_up);
        ButterKnife.bind(this);


        database = FirebaseDatabase.getInstance(Constants.Ref);
        usersdb = database.getReference("Users");
        usersdb.keepSynced(false);

        validationObj = new Validation();

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
    void validateBrand(String  brand , final OnValidateInterface onPhoneValidateInterface ){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                    Log.e("validate_brand" , userModel.toString() +"  " + dataSnapshot.toString());
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
        usersdb.orderByChild("brandName").equalTo(( brand)).addListenerForSingleValueEvent(valueEventListener);
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
       try {
           if (current_valueEventListener != null) {
               usersdb.removeEventListener(current_valueEventListener);
               showloading(false);
               Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
           }
       }catch (Exception e){}
    }



    interface OnValidateInterface {

        void  onFinish(boolean state ) ;

    }


    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
