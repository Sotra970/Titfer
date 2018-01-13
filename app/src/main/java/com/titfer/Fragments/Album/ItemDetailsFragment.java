package com.titfer.Fragments.Album;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Activties.Upload.ImageUploadActivity;
import com.titfer.Activties.Service.CallbackWithRetry;
import com.titfer.Activties.Service.Injector;
import com.titfer.Activties.Service.onRequestFailure;
import com.titfer.Activties.Insert.WriteCommentActivity;
import com.titfer.Activties.cahting.MessageRoomDetailsActivity;
import com.titfer.FCM.FCM_Notfication_OBJ;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CartModel;
import com.titfer.Models.CommentModel;
import com.titfer.Models.NotificationModel;
import com.titfer.Models.Room_model;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.Add_album_listener;
import com.titfer.intefraces.HolderListener;
import com.titfer.internal_db.Room_tabel;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ItemDetailsFragment extends Fragment implements HolderListener , Add_album_listener {




    public ItemDetailsFragment() {
        // Required empty public constructor
    }


    AlbumModel itemModel;
    AlbumModel albumModel;
    FirebaseDatabase database  ;
    View res_layout  ;

    UserModel userModel;

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public void setItemModel(AlbumModel itemModel) {
        this.itemModel = itemModel;
    }
    public void setAlbumModel(AlbumModel albumModel) {
        this.albumModel = albumModel;
    }

    boolean isMine = false ;
    String from_tab ;

    public void setFrom_tab(String from_tab) {
        this.from_tab = from_tab;
    }

    @BindView(R.id.bar_title)
    TextView bar_title ;

    @BindView(R.id.fav_icon)
    ImageView fav_icon ;

    @BindView(R.id.caption)
    TextView caption ;
   @BindView(R.id.edcaption)
    EditText ed_caption ;

    @BindView(R.id.cover)
    ImageView cover ;

    @BindView(R.id.edit_caption_done)
    ImageView edit_caption_done ;



    @BindView(R.id.edit_caption)
    View edit_caption ;


    @BindView(R.id.delete_photo)
    View delete_photo ;

    @BindView(R.id.settings)
    View settings ;

    @BindView(R.id.add_recomendation)
    View add_recom ;
    @BindView(R.id.remove_recomendation)
    View remove_recom ;


    @OnClick(R.id.bar_back)
    void bar_back()
    {

        getActivity().onBackPressed();
    }

    @OnClick(R.id.add_recomendation)
    void add_rec(){
        showloading(true);
        database.getReference("Recommendations")
                .child(itemModel.getId())
                .child(albumModel.getId())
                .child(userModel.getId())
                .setValue(true);

    }

    @OnClick(R.id.remove_recomendation)
    void remove_recomendation(){
        showloading(true);
        database.getReference("Recommendations")
                .child(itemModel.getId())
                .child(albumModel.getId())
                .child(userModel.getId())
                .setValue(null);
    }


    @OnClick(R.id.edit_caption)
    void edit_caption(){
        edit_caption_done.setVisibility(View.VISIBLE);
        ed_caption.setVisibility(View.VISIBLE);
        ed_caption.requestFocus();
        caption.setVisibility(View.GONE);
        showsettings();
    }


    @OnClick(R.id.delete_photo)
    void delete_photo(){
        showsettings();
        showloading(true) ;
        database.getReference("AlbumsItems/"+albumModel.getId()+"/"+itemModel.getId())
                .setValue(null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext() , "item not deleted pleas try again " , Toast.LENGTH_LONG).show();
            }
        });
    }


    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


    private void showloading(final boolean show) {
        try {
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
        }catch (Exception e){}
    }

    @OnClick(R.id.edit_caption_done)
    void edit_caption_done(){
        if (TextUtils.isEmpty(ed_caption.getText().toString())){
            ed_caption.setError(getString(R.string.empty_field_caption));
            return;
        }
        ed_caption.setError(null);
        edit_caption_done.setVisibility(View.GONE);
        ed_caption.setVisibility(View.GONE);
        caption.setText(ed_caption.getText().toString());
        caption.setVisibility(View.VISIBLE);
        bar_title.setText(caption.getText().toString());
        database.getReference("AlbumsItems/"+albumModel.getId()+"/"+itemModel.getId()+"/title")
                .setValue(caption.getText().toString()); ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout = inflater.inflate(R.layout.fragment_item_details, null, false);
            ButterKnife.bind(this , res_layout) ;

            if (savedInstanceState !=null){
                itemModel = (AlbumModel) savedInstanceState.getSerializable("itemModel");
                albumModel = (AlbumModel) savedInstanceState.getSerializable("albumModel");
                userModel = (UserModel) savedInstanceState.getSerializable("userModel");
                isMine = savedInstanceState.getBoolean("isMine");
                from_tab = savedInstanceState.getString("from");

            }else {
                if (AppContoller.getInstance().getPrefManager().getUser().getId() == userModel.getId()){
                    isMine = true ;
                }
              //  getLikes();
                get_data();

            }


            comments_setup();


            if (isMine){
                settings.setVisibility(View.VISIBLE);
            }else settings.setVisibility(View.GONE);

            if (AppContoller.getInstance().getPrefManager().getUser().getType() == 2)
            {
                check_rec();
            }

            bar_title.setText(itemModel.getTitle());

            caption.setText(itemModel.getTitle());
            ed_caption.setText(itemModel.getTitle());
            Glide.with(getContext())
                    .load(itemModel.getImg())
                    .apply(new RequestOptions().fitCenter())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .transition(new DrawableTransitionOptions().crossFade())
                    .thumbnail(0.5f)
                    .into(cover);
            if (itemModel.isLiked())
                fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_solid_white));
            else
                fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_stroke_white));


        }
        return  res_layout ;
    }

    private void check_rec() {
        showloading(true);
        database.getReference("Recommendations")
                .child(itemModel.getId())
                .child(albumModel.getId())
                .child(userModel.getId())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    if (dataSnapshot.exists()){
                        remove_recom.setVisibility(View.VISIBLE);
                        add_recom.setVisibility(View.GONE);
                        showloading(false);
                    }else {
                        remove_recom.setVisibility(View.GONE);
                        add_recom.setVisibility(View.VISIBLE);
                        showloading(false);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void  getLikes(){
         likes_db = database.getReference("ItemsLikes/"+itemModel.getId()+"/"+AppContoller.getInstance().getPrefManager().getUser().getId());
        likes_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("ite_details_ilkes" , dataSnapshot.toString());

                try{
                    if (dataSnapshot.getValue() == null){
                        fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_stroke_white));
                        itemModel.setLiked(false);
                    }else {

                            fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_solid_white));
                        itemModel.setLiked(true);
                    }

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    DatabaseReference usersdb ;
    DatabaseReference likes_db ;
    void  get_data( ) {


         usersdb = database.getReference("AlbumsItems/"+albumModel.getId()+"/"+itemModel.getId());
        usersdb.keepSynced(true);

        usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showloading(false);
                try{
                    Log.e("album item",dataSnapshot.toString());
                    if (dataSnapshot.exists()){
                        AlbumModel child = dataSnapshot.getValue(AlbumModel.class) ;
                        if (!child.getTitle().equals(itemModel.getTitle())){
                            caption.setText(child.getTitle());
                            ed_caption.setText(child.getTitle());
                            itemModel.setTitle(child.getTitle());
                        }
                        if (!child.getImg().equals(itemModel.getImg())){

                            Glide.with(getContext())
                                    .load(child.getImg())
                                    .apply(new RequestOptions().fitCenter())
                                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                    .transition(new DrawableTransitionOptions().crossFade())
                                    .thumbnail(0.5f)
                                    .into(cover);

                            itemModel.setImg(child.getImg());
                        }




                    }else {
                        new AlertDialog.Builder(getActivity()).setMessage("item has been deleted ")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getActivity().onBackPressed();
                                    }
                                })
                                .setCancelable(false)
                                .create().show();
                    }

                }catch (Exception e){}

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

    @Override
    public void onHolderClicked(View view, Object child) {


    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }


    boolean settingsOpend = false ;
    @OnClick(R.id.settings)
    void showsettings(){
        if (!settingsOpend){
            settings.setClickable(false);
            edit_caption.setVisibility(View.VISIBLE);
            delete_photo.setVisibility(View.VISIBLE);


            edit_caption.animate().alpha(1).setDuration(300).setStartDelay(0).setInterpolator(new FastOutLinearInInterpolator());
            delete_photo.animate().alpha(1).setStartDelay(200).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());

            settingsOpend = true ;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    settings.setClickable(true);

                }
            } , 500);

        }else {
            settings.setClickable(false);
            edit_caption.animate().alpha(0).setStartDelay(200).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());
            delete_photo.animate().alpha(0).setDuration(300).setStartDelay(0).setInterpolator(new FastOutLinearInInterpolator());

            settingsOpend = false ;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    edit_caption.setVisibility(View.GONE);
                    delete_photo.setVisibility(View.GONE);
                    settings.setClickable(true);

                }
            } , 500);
        }

    }

    @Override
    public void onHolderClicked(View view) {
        Intent i = new Intent(getContext(), ImageUploadActivity.class);
        i.putExtra("extra", itemModel);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("itemModel", itemModel);
        outState.putSerializable("albumModel", albumModel);
        outState.putSerializable("userModel", userModel);
        outState.putBoolean("isMine", isMine);
        outState.putString("from_tab", from_tab);
    }


    @OnClick(R.id.send_comment)
    void send_comment(){
        Intent i = new Intent(getContext(), WriteCommentActivity.class);
        i.putExtra("extra",itemModel);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }





    @OnClick(R.id.fav_add)
    void fav_add(){

        boolean fav;

        if (itemModel.isLiked()){
            fav= false ;
            fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_stroke_white));
        }else {
            fav= true ;
            fav_icon.setImageDrawable(ContextCompat.getDrawable(getContext() , R.drawable.fav_solid_white));
        }

        DatabaseReference favRef =  database.getReference("ItemsLikes/"+itemModel.getId());

        favRef.child(AppContoller.getInstance().getPrefManager().getUser().getId()).setValue(fav?true : null);

        database.getReference("UserLikes/"+AppContoller.getInstance().getPrefManager().getUser().getId())
                .child(userModel.getId())
               .child(albumModel.getId()) .child(itemModel.getId()).setValue(fav?true : null);

    }

    @BindView(R.id.comments)
    RecyclerView comments ;
    FirebaseRecyclerAdapter<CommentModel, com.titfer.Fragments.Album.CommentHolder> mAdapter ;
    void comments_setup(){
        comments.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseReference commentsRef =  database.getReference("ItemsComments/"+itemModel.getId());
        mAdapter = new FirebaseRecyclerAdapter<CommentModel, CommentHolder>(
                CommentModel.class,
                R.layout.comment_item,
                CommentHolder.class,
                commentsRef) {
            @Override
            public void populateViewHolder(CommentHolder holder, CommentModel commentModel, int position) {

                holder.setText(commentModel.getComment());
                holder.setName(commentModel.getUser_name());
                holder.setImg(commentModel.getImg());

            }
        };

        comments.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mAdapter.cleanup();
            usersdb.keepSynced(false);
        }catch (Exception e){}
    }



    @OnClick(R.id.add_cart)
    void add_cart(){
        final String prod_path  = "ProdChat/"+AppContoller.getInstance().getPrefManager().getUser().getId() + "/" + itemModel.getId()
                +"/"+ userModel.getId()
                ;
        final String prod_path2  = "ProdChat/"+ userModel.getId() + "/" + itemModel.getId()
                +"/"+AppContoller.getInstance().getPrefManager().getUser().getId();
        database.getReference(prod_path)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Room_model room_model ;
                        if (!dataSnapshot.exists()){

                            String key  = database.getReference(prod_path).push().getKey() ;
                            CartModel cartModel =  new CartModel (
                                    key ,
                                    itemModel.getTitle() ,
                                    itemModel.getImg() ,
                                    AppContoller.getInstance().getPrefManager().getUser().getId()
                                    , userModel.getId()
                                    , userModel.getFirstName()+" "+ userModel.getLastName()
                                    ,AppContoller.getInstance().getPrefManager().getUser().getFirstName()+" "+AppContoller.getInstance().getPrefManager().getUser().getLastName()

                            ) ;

                            database.getReference("Cart/"+AppContoller.getInstance().getPrefManager().getUser().getId())
                                    .child(key).setValue(cartModel) ;

                             room_model = new Room_model(key ,AppContoller.getInstance().getPrefManager().getUser().getId() , userModel.getId());

                            database.getReference(prod_path).child(key).setValue(room_model) ;
                            database.getReference(prod_path2).child(key).setValue(room_model) ;
                            add_notfication(cartModel);

                        }else {
                            room_model =  dataSnapshot.getChildren().iterator().next().getValue(Room_model.class) ;
                            Log.e("prod_path"  , dataSnapshot.toString() +"");
                            Log.e("prod_path"  , dataSnapshot.getChildren().iterator().next().getKey() +"");
                        }

                        final Room_tabel room_tabel = new Room_tabel() ;
                        room_tabel.room_id = room_model.room_id ;
                        room_tabel.consumer_id = room_model.consumer_key ;
                        room_tabel.designer_id = room_model.designer_key ;
                        room_tabel.img = userModel.getProfilePic() ;
                        room_tabel.name = userModel.getFirstName() +" "+ userModel.getLastName();
                        room_tabel.type = "prod" ;
                        if (!room_tabel.check(room_model.room_id)){
                            room_tabel.save();
                        }
                        Intent intent = new Intent(getActivity() , MessageRoomDetailsActivity.class) ;
                        intent.putExtra("room_id" , room_model.room_id) ;
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }




    void add_notfication(CartModel cartModel){
        String name = AppContoller.getInstance().getPrefManager().getUser().getFirstName() +" " + AppContoller.getInstance().getPrefManager().getUser().getLastName() ;
        String message = name +" has ordered " +cartModel.getTitle() ;
        String message_admin = name +" has ordered item " +cartModel.getTitle() + " from designer " + cartModel.getDesigner_name();

        NotificationModel notificationModel = new NotificationModel(message  , AppContoller.getInstance().currentDateFormat());
        NotificationModel notificationModel2 = new NotificationModel(message_admin  , AppContoller.getInstance().currentDateFormat());

        database.getReference("Notification/"+cartModel.getDesigner_key()).push().setValue(notificationModel) ;
        database.getReference("AdminNotification").push().setValue(notificationModel2) ;
        send_notfication(notificationModel2,"admin","admin");
        send_notfication(notificationModel,cartModel.getDesigner_key(),cartModel.getId());
    }

    private void send_notfication(NotificationModel notificationModel , String user_id  , String room_id) {
        NotificationModel  model  = notificationModel  ;
        model.setAction("notification");
        model.setUser_id(user_id);
        Call<JSONObject> jsonObjectCall  = Injector.FcmApi().send_notification(
                new FCM_Notfication_OBJ(
                        "/topics/user_notifications"
                        ,model
                )
        );
        jsonObjectCall.enqueue(new CallbackWithRetry<JSONObject>(5, 3000, jsonObjectCall, new onRequestFailure() {
            @Override
            public void onFailure() {
                Log.e("send_notification" , "fail");
            }
        }) {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e("send_notification" , "sucess " + response.body().toString());
            }
        });

    }



//

    }





