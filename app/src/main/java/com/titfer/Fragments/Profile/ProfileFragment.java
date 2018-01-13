package com.titfer.Fragments.Profile;


import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.titfer.Activties.Upload.AlbumUploadActivity;
import com.titfer.Activties.cahting.MessageRoomDetailsActivity;
import com.titfer.Activties.signup.EditCustomerActivity;
import com.titfer.Activties.signup.EditProffSignUpActivity;
import com.titfer.Fragments.Album.AlbumDetailsFragment;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.adapter.AlbumAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;
import com.titfer.intefraces.Add_album_listener;
import com.titfer.intefraces.HolderListener;
import com.titfer.internal_db.Room_tabel;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements HolderListener , Add_album_listener  , ConnStatesCallBack.FirebaseConnectionState{

    @BindView(R.id.call_btn_profile)
    View call ;

    @BindView(R.id.edit_profile_info)
    View edit_profile_info ;
    @BindView(R.id.profile_img_edit)
    View profile_img_edit ;


    @BindView(R.id.messa_btn_profile)
    View message ;

    @BindView(R.id.bio)
    TextView bio ;


    @BindView(R.id.ed_bio)
    EditText ed_bio ;
    @BindView(R.id.verfied)
    View verfied ;


    @BindView(R.id.brandName)
    TextView brandName ;


    @BindView(R.id.designer_Name)
    TextView designer_Name ;


    @BindView(R.id.designer_address)
    TextView designer_address ;

    @BindView(R.id.albums)
    RecyclerView albums ;
    @BindView(R.id.profile_img)
    ImageView profile_img ;

    AlbumAdapter albumAdapter ;


    public ProfileFragment() {
        // Required empty public constructor
    }

    ArrayList<AlbumModel> albums_list = new ArrayList<>() ;


    UserModel userModel;
    FirebaseDatabase database  ;
    View res_layout  ;
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    boolean is_mine = false ;
    String from_tab ;

    public void setFrom_tab(String from_tab) {
        this.from_tab = from_tab;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout = inflater.inflate(R.layout.fragment_profile, container, false);
            ButterKnife.bind(this , res_layout) ;

            if (savedInstanceState !=null){
                userModel = (UserModel) savedInstanceState.getSerializable("userModel");
                is_mine = savedInstanceState.getBoolean("isMine");
                from_tab = savedInstanceState.getString("from");
                albums_list = (ArrayList<AlbumModel>) savedInstanceState.getSerializable("albums_list");


            }else {
                if (AppContoller.getInstance().getPrefManager().getUser().getId().equals(userModel.getId())){
                    is_mine = true ;
                }
                get_profileExtras();
            }

            if (AppContoller.getInstance().getPrefManager().getUser().getId().equals(userModel.getId())){
                is_mine = true ;
            }

            Log.e("profile_fragment" ,"savedInstanceState "+savedInstanceState) ;

            Log.e("profile userModel" ,is_mine + " ") ;

            setup_user();

            bio.setText(userModel.getBio());
            brandName.setText(userModel.getBrandName());
            Log.e("brand" , userModel.getBrandName()+"") ;
            designer_address.setText(userModel.getCountry());
            designer_Name.setText(userModel.getFirstName()+" " + userModel.getLastName());
            Glide.with(getContext()).load(userModel.getProfilePic())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().circleCrop())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(profile_img) ;


            recycler_setup();


        }
        return  res_layout ;
    }




    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;

    @BindView(R.id.no_search_results)
    TextView no_search_results ;

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


    void recycler_setup (){
        albumAdapter = new AlbumAdapter(albums_list , getContext() , this , this , is_mine) ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),2) ;
        albums.setLayoutManager(gridLayoutManager);
        albums.setAdapter(albumAdapter);

    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (AlbumModel  user : albums_list) {
            if (user.getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_profileExtras( ) {



                database.getReference("Users")
                        .child(userModel.getId()).child("verified").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            userModel.verified = dataSnapshot.getValue(Boolean.class) ;
                        }else {
                            userModel.verified = false ;
                        }
                        bind_vf_icon();
                        get_albums();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }








    void  get_albums( ) {
        albums_list.clear();
        albumAdapter.notifyDataSetChanged();

        Query usersdb = database.getReference("Albums/"+ userModel.getId());
        usersdb.keepSynced(true);

        showloading(true);


        usersdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildren() == null && !is_mine && userModel.getType() != 0)
                    no_search_results.setVisibility(View.VISIBLE);

                database.getReference("UserFollow/"+AppContoller.getInstance().getPrefManager().getUser().getId())
                        .child(userModel.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            userModel.followed = "true" ;
                        }else {
                            userModel.followed = null ;
                        }
                        bind_follow_icons();
                        showloading(false);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        usersdb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("profile_albums_ad" , dataSnapshot.toString()) ;
                albums_list.add(dataSnapshot.getValue(AlbumModel.class));
                albumAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("profile_albums_ch" , dataSnapshot.toString() + s) ;
                AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class)  ;
                int pos = getIndexForKey(albumModel.getId());
                albums_list.set(pos,albumModel);
                pos = is_mine ? pos+1 : pos;
                albumAdapter.notifyItemChanged(pos);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.e("profile_albums_ch" , dataSnapshot.toString()) ;
                AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class)  ;
                int pos = getIndexForKey(albumModel.getId());
                albums_list.remove(pos);
                pos = is_mine ? pos+1 : pos;
                albumAdapter.notifyItemRemoved(pos);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;





    }

    @Override
    public void onHolderClicked(View view, Object child) {
      try {
          Log.e("onHolderClicked" , "onHolderClicked");
          Log.e("from_tab" , "from_tab :" +from_tab);
          AlbumDetailsFragment searchDesignersListFragment = new AlbumDetailsFragment() ;
          searchDesignersListFragment.setUserModel(userModel);
          searchDesignersListFragment.setAlbumModel((AlbumModel) child);
          searchDesignersListFragment.setFrom_tab(from_tab);
          FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
          fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
          if (from_tab.contains("search"))
              fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
          else if (from_tab.contains("main"))
              fragmentTransaction.replace(R.id.home_content_frame, searchDesignersListFragment);
           else
              fragmentTransaction.replace(R.id.profile_content_frame, searchDesignersListFragment);
          fragmentTransaction.addToBackStack(from_tab+"_album_details");
          fragmentTransaction.commit();
      }catch (Exception e ){
        Log.e("ProfileFragment" , "onHolderClicked (e)->"+e.toString() );
      }

    }

    @Override
    public void onHolderLongClicked(View view, Object child) {
        Log.e("onHolderClicked" , "onHolderClickedlong");

        try{
            final AlbumModel albumModel = (AlbumModel) child ;

            new  AlertDialog.Builder(getActivity())
                    .setMessage("Are you want to delete album ")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showloading(true);
                    database.getReference("Albums/"+AppContoller.getInstance().getPrefManager().getUser().getId()).child(albumModel.getId())
                            .setValue(null)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    showloading(false);

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showloading(false);
                                }
                            });
                }
            }).create().show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onHolderClicked(View view) {
        Intent i = new Intent(getContext(), AlbumUploadActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("userModel", userModel);
        outState.putBoolean("isMine", is_mine);
        outState.putString("from", from_tab);
        outState.putSerializable("albums_list" , albums_list);
    }


    @BindView(R.id.edit_bio_done)
    View edit_bio_done ;

    @BindView(R.id.edit_bio_icon)
    View edit_bio_icon ;


    @OnClick(R.id.edit_bio_icon)
    void edit_caption(){
        edit_bio_done.setVisibility(View.VISIBLE);
        ed_bio.setVisibility(View.VISIBLE);
        ed_bio.requestFocus();
        bio.setVisibility(View.GONE);
        edit_bio_icon.setVisibility(View.GONE);
    }

    @OnClick(R.id.edit_bio_done)
    void edit_bio_done(){
        if (TextUtils.isEmpty(ed_bio.getText().toString())){
            ed_bio.setError(null);
            edit_bio_done.setVisibility(View.GONE);
            ed_bio.setVisibility(View.GONE);
            bio.setVisibility(View.VISIBLE);
            edit_bio_icon.setVisibility(View.VISIBLE);
            return;
        }
        ed_bio.setError(null);
        edit_bio_done.setVisibility(View.GONE);
        ed_bio.setVisibility(View.GONE);
        bio.setText(ed_bio.getText().toString());
        UserModel userModel = AppContoller.getInstance().getPrefManager().getUser();
        userModel.setBio(ed_bio.getText().toString());
        AppContoller.getInstance().getPrefManager().storeUser(userModel);
        bio.setVisibility(View.VISIBLE);
        edit_bio_icon.setVisibility(View.VISIBLE);
        database.getReference("Users/"+AppContoller.getInstance().getPrefManager().getUser().getId()+ "/bio")
                .setValue(bio.getText().toString());

    }


    @OnClick(R.id.profile_img_edit)
    void pick_image_permetion() {
        // Here, thisActivity is the current activity
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Config.MY_PERMISSIONS_REQUEST_STORAGE);
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
                    Toast.makeText(getActivity()," Storage permission id needed  to get into your images ", Toast.LENGTH_SHORT).show();
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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, Config.RESULT_LOAD_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /// select from gallery section
        if (requestCode == Config.RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && data!=null) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Log.e("Uri", selectedImage + "");
            Log.e("filePathColumn", MediaStore.Images.Media.DATA + "");

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
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
            options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            options.setToolbarTitle(getString(R.string.edit_img));
            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            options.withMaxResultSize(420, 420);


            UCrop.of(selectedImage, Uri.fromFile(get_uri()))
                    .withOptions(options)
                    .useSourceImageAspectRatio()
                    .withMaxResultSize(420,420)
                    .start(getActivity() , UCrop.REQUEST_CROP);
        }
        else if (requestCode == UCrop.REQUEST_CROP) {

        }else {
            Log.e(TAG, "unnon ");

        }

    }

    String TAG = "profile_img_upload";
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(getContext(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), R.string.toast_unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }


    private void handleCropResult(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {


            Log.e(TAG, "handleCropResult: "+resultUri);


            upload(resultUri) ;
        } else {
            Toast.makeText(getContext(), R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show();
        }
    }

    @BindView(R.id.progressBar)
    ProgressBar progressBar ;
    @BindView(R.id.progressBarView)
    View progressBarView ;
    @BindView(R.id.progressBarText)
    TextView progressBarText ;

    private void upload(Uri resultUri) {
        File file =  new File(resultUri.getPath()) ;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("images/"+file.getName());

        UploadTask uploadTask = riversRef.putFile(resultUri);


        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e("upload onFailure"  , exception.toString()) ;
                progressBarView.setVisibility(View.GONE);
                Toast.makeText(getContext() , "Upload not succeeded" , Toast.LENGTH_LONG) .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("upload onSuccess"  ,taskSnapshot.getDownloadUrl() +"" )  ;
                final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                current_valueEventListener = true ;
                database.getReference("Users/"+AppContoller.getInstance().getPrefManager().getUser().getId()+"/profilePic").setValue(downloadUrl.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressBarView.setVisibility(View.GONE);
                        UserModel userModel = AppContoller.getInstance().getPrefManager().getUser();
                        userModel.setProfilePic(downloadUrl.toString());
                        AppContoller.getInstance().getPrefManager().storeUser(userModel);
                        Glide.with(getContext())
                                .asDrawable()
                                .load(downloadUrl)
                                .transition( new DrawableTransitionOptions().crossFade())
                                .thumbnail(0.5f)
                                .apply(new RequestOptions().fitCenter())
                                .apply(new RequestOptions().circleCrop())
                                .into(profile_img) ;
                Toast.makeText(getContext() , "image has updated  succeeded" , Toast.LENGTH_LONG) .show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (  taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progress = progress  *100.0 ;
                Log.e("upload onProgress"  ,taskSnapshot.getBytesTransferred() + "ba2y" +taskSnapshot.getTotalByteCount() +"  p "+ progress) ;
                progressBar.setVisibility(View.VISIBLE);
                progressBarView.setVisibility(View.VISIBLE);
                // updating progress bar value
//                progressBar.setProgress((int) progress);
//                progressBarText.setText((int) progress+" %") ;


            }
        });


    }




    protected File get_uri()  {
//        File outputDir = Environment.getExternalStorageDirectory(); // context being the Activity pointer
        File outputDir = getActivity().getCacheDir(); // context being the Activity pointer
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
            progressBarView.setVisibility(View.GONE);
            Toast.makeText(getContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e(TAG, "REQUEST_CROP ");

            if (intent.getAction().equals(Config.PROFILE_CROP)) {
                handleCropResult(intent);
            }

        }
    };


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver ,new IntentFilter( Config.PROFILE_CROP));
        try{

            setup_user();
        }catch (Exception e){}
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver  ) ;
    }



    @OnClick(R.id.edit_profile_info)
    void edit_profile_info(){
        Intent i = null;
        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2)
            return;
        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 1)
         i = new Intent(getContext(), EditProffSignUpActivity.class);
        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 0)
            i = new Intent(getContext(), EditCustomerActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @BindView(R.id.follow)
    View foloow_bt ;

    void setup_user(){

        if (is_mine){
            message.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
            edit_profile_info.setVisibility(View.VISIBLE);
            edit_profile_info.setVisibility(View.VISIBLE);
            foloow_bt.setVisibility(View.GONE);
            profile_img_edit.setVisibility(View.VISIBLE);
            edit_bio_icon.setVisibility(View.VISIBLE);
        }else {
            edit_profile_info.setVisibility(View.GONE);
            foloow_bt.setVisibility(View.VISIBLE);
            profile_img_edit.setVisibility(View.GONE);

        }


        if (userModel.getType() == 0)
            verfied.setVisibility(View.GONE);


        bind_vf_icon();
        bio.setText(userModel.getBio());
        brandName.setText(userModel.getBrandName());
        Log.e("brand" , userModel.getBrandName() +"") ;
        designer_address.setText(userModel.getCountry());
        designer_Name.setText(userModel.getFirstName()+" " + userModel.getLastName());
        Glide.with(getContext()).load(userModel.getProfilePic())
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().centerCrop())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(profile_img) ;

    }




    @OnClick(R.id.messa_btn_profile)
    void open_message_activity(){
        final String prod_path  = "Chat/"+AppContoller.getInstance().getPrefManager().getUser().getId()
                +"/"+ userModel.getId();
        final String prod_path2  = "Chat/"+ userModel.getId()
                +"/"+AppContoller.getInstance().getPrefManager().getUser().getId();
        database.getReference(prod_path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String key ;
                        if (!dataSnapshot.exists()){
                            key  = database.getReference(prod_path).push().getKey() ;
                            database.getReference(prod_path).child(key).setValue(true) ;
                            database.getReference(prod_path2).child(key).setValue(true) ;

                        }else {
                            Log.e("prod_path"  , dataSnapshot.toString() +"");
                            Log.e("prod_path"  , dataSnapshot.getChildren().iterator().next().getKey() +"");
                            key = dataSnapshot.getChildren().iterator().next().getKey();
                        }

                        final Room_tabel room_tabel = new Room_tabel() ;
                        room_tabel.room_id = key ;
                        room_tabel.img = userModel.getProfilePic() ;
                        room_tabel.name = userModel.getFirstName() +" "+ userModel.getLastName();
                        room_tabel.type = "chat" ;
                        if (!room_tabel.check(key)){
                            room_tabel.save();
                        }
                        Intent intent = new Intent(getActivity() , MessageRoomDetailsActivity.class) ;
                        intent.putExtra("room_id" , key) ;
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right , R.anim.exit_to_left);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }





    @BindView(R.id.follow_icon)
    ImageView follow_icon ;
    @OnClick(R.id.follow)
    void fav_add(){

        boolean fav;

        if (userModel.is_followed()){
            fav= false ;
            follow_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_add_white_24dp));
            userModel.followed = null ;
        }else {
            fav= true ;
            follow_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_done_white_24dp));
            userModel.followed = "true" ;
        }

        database.getReference("UserFollow/"+AppContoller.getInstance().getPrefManager().getUser().getId())
                 .child(userModel.getId()).setValue(fav?true : null);

    }


    void bind_follow_icons(){
        if (!userModel.is_followed()){
            follow_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_add_white_24dp));
        }else {
            follow_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_done_white_24dp));
        }
    }



    @BindView(R.id.verfied_btt)
    View verfied_btt ;

    @BindView(R.id.verfied_icon)
    ImageView verfied_icon ;
    @OnClick(R.id.verfied_btt)
    void verfied_btt(){


        if (userModel.is_verified()){
            userModel.verified = false ;
        }else {
            userModel.verified = true ;
        }
        bind_vf_icon();
        database.getReference("Users")
                .child(userModel.getId()).child("verified").setValue(userModel.verified);

    }


    void bind_vf_icon(){
        if (!userModel.is_verified()){
            verfied.setVisibility(View.GONE);
            verfied_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_add_white_24dp));
        }else {
            verfied.setVisibility(View.VISIBLE);
            verfied_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.ic_done_white_24dp));

        }

        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2){
            verfied_btt.setVisibility(View.VISIBLE);
        }else{
            verfied_btt.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.call_btn_profile)
    void call_user(){
        if (userModel.getPhone() != null){
        Uri call = Uri.parse("tel:" + userModel.getPhone());
        Intent surf = new Intent(Intent.ACTION_DIAL, call);
        startActivity(surf);
        }
    }

}
