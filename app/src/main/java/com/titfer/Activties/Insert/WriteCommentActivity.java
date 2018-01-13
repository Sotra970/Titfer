package com.titfer.Activties.Insert;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CommentModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WriteCommentActivity extends AppCompatActivity implements com.titfer.app.ConnStatesCallBack.FirebaseConnectionState {

    FirebaseDatabase database ;


    @BindView(R.id.title_input)
    EditText title_input ;


    @BindView(R.id.done)
    View done ;

    @BindView(R.id.cancel)
    View cancel ;



    AlbumModel extra  ;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("albumModel", extra );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        ButterKnife.bind(this) ;
        if (savedInstanceState == null)
        extra = (AlbumModel)  getIntent().getExtras().get("extra") ;
        else extra = (AlbumModel) savedInstanceState.getSerializable("albumModel");

        database = FirebaseDatabase.getInstance(Constants.Ref);


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

    @OnClick(R.id.cancel)
    void cancel(){
        onBackPressed();

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

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



    @OnClick(R.id.done)
    void done(){
        if (TextUtils.isEmpty(title_input.getText().toString())){
            return;
        }
        showloading(true);
        DatabaseReference commentsRef =  database.getReference("ItemsComments/"+extra.getId());
        String key = commentsRef.push().getKey() ;
        CommentModel commentModel = new   CommentModel(title_input.getText().toString(),
                AppContoller.getInstance().getPrefManager().getUser().getProfilePic(),
                AppContoller.getInstance().getPrefManager().getUser().getFirstName()
                        +""+
                        AppContoller.getInstance().getPrefManager().getUser().getLastName()
        );
        commentModel.setId(key);
        commentsRef.child(key).setValue(commentModel);
        title_input.setText("");

        current_valueEventListener = true ;
        database.getReference("UserComments/"+AppContoller.getInstance().getPrefManager().getUser().getId())
                .push().child(key).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        });



    }


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

    @Override
    public void connected() {

    }

    boolean current_valueEventListener = false ;

    com.titfer.app.ConnStatesCallBack ConnStatesCallBack = new ConnStatesCallBack() ;
    @Override
    public void disConnected() {
        if (current_valueEventListener) {
            current_valueEventListener = false ;
            FirebaseDatabase.getInstance().purgeOutstandingWrites();
            showloading(false);
            Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }




}
