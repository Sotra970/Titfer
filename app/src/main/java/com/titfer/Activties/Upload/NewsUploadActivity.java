package com.titfer.Activties.Upload;

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
import com.titfer.Models.NewsModel;
import com.titfer.R;
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

public class NewsUploadActivity extends AppCompatActivity implements com.titfer.app.ConnStatesCallBack.FirebaseConnectionState {

    FirebaseDatabase database ;
    DatabaseReference albums_db ;

    @BindView(R.id.cover)
    ImageView cover ;

    @BindView(R.id.title_input)
    EditText title_input ;

    @BindView(R.id.desc_input)
    EditText desc_input ;


    @BindView(R.id.done)
    View done ;

    @BindView(R.id.cancel)
    View cancel ;


    Uri imge_name_url ;


    @BindView(R.id.progressBar)
    ProgressBar progressBar ;
    @BindView(R.id.progressBarView)
    View progressBarView ;
    @BindView(R.id.progressBarText)
    TextView progressBarText ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_upload);
        ButterKnife.bind(this) ;

        database = FirebaseDatabase.getInstance(Constants.Ref);
        albums_db = database.getReference("News");


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


    @OnClick(R.id.add_cover)
     void pick_image_permetion() {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},Config.MY_PERMISSIONS_REQUEST_STORAGE);
        }else {
            pick_img();
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Config.MY_PERMISSIONS_REQUEST_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    pick_img();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this," Storage permission id needed  to get into your images ", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }





    void pick_img(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, Config.RESULT_LOAD_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /// select from gallery section
        if (requestCode == Config.RESULT_LOAD_IMAGE && resultCode == this.RESULT_OK && data!=null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Log.e("Uri", selectedImage + "");
            Log.e("filePathColumn", MediaStore.Images.Media.DATA + "");

            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            cursor.close();

            Log.e("picturePath", picturePath + "");

            UCrop.Options options = new UCrop.Options();
            options.setCompressionQuality(90);
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            options.setAllowedGestures(UCropActivity.SCALE , UCropActivity.NONE , UCropActivity.ALL);
            options.setFreeStyleCropEnabled(false);
            options.setShowCropFrame(true);
            options.setAspectRatioOptions(0 , new AspectRatio[]{

                    new AspectRatio("1:1"  , 1f , 1f) ,
                    new AspectRatio("16:9"  , 16f , 9f) ,
                    new AspectRatio("4:3"  , 4f , 3f)
            });
            options.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            options.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            options.setToolbarTitle(getString(R.string.edit_img));
            options.setToolbarWidgetColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            options.withMaxResultSize(1080, 1080);


            UCrop.of(selectedImage, Uri.fromFile(get_uri()))
                    .withOptions(options)
                    .useSourceImageAspectRatio()
                    .withMaxResultSize(420,420)
                    .start(this);
        }
        else if (requestCode == UCrop.REQUEST_CROP) {

            if (resultCode == Activity.RESULT_OK) {
                handleCropResult(data);
            }
            else if (resultCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }
        }

    }

        String TAG = "upload_album";
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {


            Glide.with(this)
                    .asDrawable()
                    .load(resultUri)
                    .transition( new DrawableTransitionOptions().crossFade())
                    .thumbnail(0.5f)
                    .apply(new RequestOptions().fitCenter())
                    .apply(new RequestOptions().circleCrop())
                    .into(cover) ;

            upload(resultUri) ;
        } else {
            Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    private void upload(Uri resultUri) {
        File file =  new File(resultUri.getPath()) ;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
            StorageReference riversRef = storageRef.child("images/"+file.getName());

        UploadTask  uploadTask = riversRef.putFile(resultUri);


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("upload onFailure"  , exception.toString()) ;
                progressBarView.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext() , "Upload not succeeded" , Toast.LENGTH_LONG) .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("upload onSuccess"  ,taskSnapshot.getDownloadUrl() +"" )  ;
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imge_name_url = downloadUrl ;
                progressBarView.setVisibility(View.GONE);
                validate_img();
                Toast.makeText(getApplicationContext() , "Upload  succeeded" , Toast.LENGTH_LONG) .show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (  taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progress = progress  *100.0 ;
                Log.e("upload onProgress"  ,taskSnapshot.getBytesTransferred() + "ba2y" +taskSnapshot.getTotalByteCount() +"  p "+ progress) ;
//                progressBar.setVisibility(View.VISIBLE);
                progressBarView.setVisibility(View.VISIBLE);
                // updating progress bar value
//                progressBar.setProgress((int) progress);
//                progressBarText.setText((int) progress+" %") ;


            }
        });


    }

    private boolean validate_img() {
        if (imge_name_url == null){
            Toast.makeText(getApplicationContext() , "please select album cover to upload " , Toast.LENGTH_LONG).show();
            return false;
        }else return true;
    }


    protected File get_uri()  {
//        File outputDir = Environment.getExternalStorageDirectory(); // context being the Activity pointer
        File outputDir = this.getCacheDir(); // context being the Activity pointer
        File outputFile = null;
        try {
            outputFile = File.createTempFile(currentDateFormat(),".jpg" , outputDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputFile;
    }
    public static String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.ENGLISH);
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }

    @OnClick(R.id.done)
    void done(){
        if (TextUtils.isEmpty(title_input.getText().toString())){
            title_input.setError(getString(R.string.empty_field_tile));
            title_input.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(desc_input.getText().toString())){
            desc_input.setError(getString(R.string.empty_field_desc));
            desc_input.requestFocus();
            return;
        }
        if (!validate_img())
            return;

        showloading(true);
       String key =  albums_db.push().getKey() ;
        NewsModel albumModel = new NewsModel(title_input.getText().toString() , desc_input.getText().toString() , imge_name_url+"") ;
        albumModel.setId(key);
        current_valueEventListener = true ;
        albums_db.child(key).setValue(albumModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });


    }

    @OnClick(R.id.cancel)
    void cancel(){
        onBackPressed();

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
        super.onBackPressed();
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



}
