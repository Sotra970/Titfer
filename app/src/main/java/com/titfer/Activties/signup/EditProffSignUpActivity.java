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
import com.titfer.app.AppContoller;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditProffSignUpActivity extends AppCompatActivity  implements ConnStatesCallBack.FirebaseConnectionState {

    // strings to store data form edit text
    String firstName, lastName, email, phone, password, brandName, profession , country, specialty1, specialty2, specialty_search_key ,   uid;
    ArrayList<String> subCat = new ArrayList<>() ;
    int professionChecked = -1;
    int countryChecked = -1;
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
    @BindView(R.id.register_prof_btn)
    Button registerBtn;
    private String old_specialty1 , old_specialty2 , old_password   ,old_proff ;


    @OnClick(R.id.profession_btn)
    public void pickProfession(View view){

        final  String[] professionList  = getResources().getStringArray(R.array.professionList);
        int index = -1 ;
        for (String s : professionList){
            index++ ;
            professionChecked = s.equals(profession) ? index: -1 ;
        }
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

        int index = -1 ;
        for (String s : countryList){
            index++ ;
            countryChecked = s.equals(country) ? index: -1 ;
        }

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
                        Toast.makeText(EditProffSignUpActivity.this,getString(R.string.at_most_2),Toast.LENGTH_SHORT).show();
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


        if (!countryIsChecked){

            county_btn.setError(getString(R.string.empty_chicked_country));
            Toast.makeText(EditProffSignUpActivity.this,getString(R.string.empty_chicked_country),Toast.LENGTH_SHORT).show();
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
        } else {
            passwordInput.setError(null);

        }

        if (!professionIsChecked){

            professionBtn.setError(getString(R.string.empty_chicked_proffession));
            Toast.makeText(EditProffSignUpActivity.this,getString(R.string.empty_chicked_proffession),Toast.LENGTH_SHORT).show();
            return;
        }else {
            professionBtn.setError(null);
        }
        if (subCat.size() != 2){
            professionBtn.setError(getString(R.string.at_least_2));
            Toast.makeText(EditProffSignUpActivity.this,getString(R.string.at_least_2),Toast.LENGTH_SHORT).show();
            return;
        }else {
            specialty1 = subCat.get(0);
            specialty2 = subCat.get(1);
            specialty_search_key = specialty1+"_"+specialty2 ;
            professionBtn.setError(null);
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
                                AppContoller.getInstance().getPrefManager().getUser().getBrandName()
                                ,
                                profession, specialty1, specialty2 , specialty_search_key, 1,
                                AppContoller.getInstance().getPrefManager().getUser().getProfilePic()
                                );
                        userModel.setCountry(country);
                        userModel.setBio(AppContoller.getInstance().getPrefManager().getUser().getBio());
                        userModel.setId(AppContoller.getInstance().getPrefManager().getUser().getId());
                        usersdb.child(userModel.getId())
                                .setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("firebase", "Registration Done");
                                showloading(false);
                                Toast.makeText(getApplicationContext() , "updated" , Toast.LENGTH_SHORT).show();


                                if (!old_specialty1.equals(userModel.getSpecialty1()))
                                {

                                    database.getReference(old_specialty1).
                                            child(AppContoller.getInstance().getPrefManager().getUser().getId()).
                                            setValue(null) ;

                                    DatabaseReference sp1_ref    = database.getReference(specialty1) ;
                                    sp1_ref.child(AppContoller.getInstance().getPrefManager().getUser().getId()).setValue(true) ;
                                }

                                if (!old_specialty2.equals(userModel.getSpecialty2()))
                                {

                                    database.getReference(old_specialty2).
                                            child(AppContoller.getInstance().getPrefManager().getUser().getId()).
                                            setValue(null) ;


                                    DatabaseReference sp2_ref    = database.getReference(specialty2) ;
                                    sp2_ref.child(AppContoller.getInstance().getPrefManager().getUser().getId()).setValue(true) ;

                                }
                                if (old_proff.equals(getString(R.string.Hairdressers))  && !userModel.getProfession().equals(getString(R.string.Hairdressers)) ){
                                    DatabaseReference sp3_ref    = database.getReference(getString(R.string.Hairdressers)) ;
                                    sp3_ref.child(AppContoller.getInstance().getPrefManager().getUser().getId()).setValue(null) ;

                                }



                                if (userModel.getProfession().equals(getString(R.string.Hairdressers))  && !old_proff.equals(getString(R.string.Hairdressers)) ){
                                    DatabaseReference sp3_ref    = database.getReference(getString(R.string.Hairdressers)) ;
                                    sp3_ref.child(AppContoller.getInstance().getPrefManager().getUser().getId()).setValue(true) ;

                                }

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
        setContentView(R.layout.activity_professional_edit);
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

                old_password = userModel.getPassword();

                county_btn.setText(userModel.getCountry());
                country = userModel.getCountry() ;

                specialtyBtn.setText(userModel.getSpecialty1() +" , " + userModel.getSpecialty2());
                specialty1 = userModel.getSpecialty1() ;
                specialty2 = userModel.getSpecialty2() ;
                old_specialty1 = userModel.getSpecialty1() ;
                old_specialty2 = userModel.getSpecialty2() ;
                old_proff = userModel.getProfession() ;

                subCat.add(specialty1) ;
                subCat.add(specialty2) ;

                professionBtn.setText(userModel.getProfession());
                profession = userModel.getProfession();

                countryIsChecked = true ;
                professionIsChecked = true ;

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
