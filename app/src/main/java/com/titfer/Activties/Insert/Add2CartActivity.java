package com.titfer.Activties.Insert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Activties.Service.CallbackWithRetry;
import com.titfer.Activties.Service.Injector;
import com.titfer.Activties.Service.onRequestFailure;
import com.titfer.FCM.FCM_CHAT_MESSAGE_OBJ;
import com.titfer.FCM.FCM_Notfication_OBJ;
import com.titfer.Models.CartModel;
import com.titfer.Models.MessageModel;
import com.titfer.Models.NotificationModel;
import com.titfer.Models.ReportModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;
import com.titfer.internal_db.Room_tabel;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Add2CartActivity extends AppCompatActivity implements com.titfer.app.ConnStatesCallBack.FirebaseConnectionState {

    FirebaseDatabase database ;


    @BindView(R.id.price_input)
    EditText price_input ;

    @BindView(R.id.date_input)
    EditText date ;


    @BindView(R.id.done)
    View done ;

    @BindView(R.id.cancel)
    View cancel ;


    String room_id ;

    Room_tabel room_tabel ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_2_cart);
        ButterKnife.bind(this) ;
        room_id = getIntent().getExtras().getString("room_id") ;
        room_tabel = new Room_tabel().get(room_id) ;
        if (savedInstanceState != null)
            room_id = savedInstanceState.getString("room_id") ;
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
        if (TextUtils.isEmpty(price_input.getText().toString())){
            price_input.setError("please enter a price ");
            return;
        }

        if (TextUtils.isEmpty(date.getText().toString())){
            date.setError("please enter a delivery date  ");
            return;
        }
        showloading(true);
        final DatabaseReference commentsRef =  database.getReference("Cart");


        current_valueEventListener = true ;
        commentsRef.child(room_tabel.consumer_id+"/"+room_tabel.room_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              final CartModel cartModel =  dataSnapshot.getValue(CartModel.class) ;
                cartModel.setPrice(price_input.getText().toString());
                cartModel.setDate(date.getText().toString());
                cartModel.setState("finished");
                add_notfication(cartModel);
                commentsRef.child(room_tabel.consumer_id+"/"+room_tabel.room_id).setValue(cartModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        onBackPressed();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showloading(false);
                        Toast.makeText(getApplicationContext() , " please check your connection" , Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void add_notfication(CartModel cartModel){
        String message = cartModel.getDesigner_name()+" has set price and delivery date for "+cartModel.getTitle() ;
        String message_admin = cartModel.getDesigner_name()+" has set price and delivery date for item "+cartModel.getTitle() + " to customer "+ cartModel.getCustomer_name();

        NotificationModel notificationModel = new NotificationModel(message  , AppContoller.getInstance().currentDateFormat());
        NotificationModel notificationModel2 = new NotificationModel(message_admin  , AppContoller.getInstance().currentDateFormat());

        database.getReference("Notification/"+cartModel.getConsumer_key()).push().setValue(notificationModel) ;
        database.getReference("AdminNotification").push().setValue(notificationModel2) ;
        send_notfication(notificationModel2,"admin", "admin");
        send_notfication(notificationModel,cartModel.getConsumer_key(),room_id);
    }

    private void send_notfication(NotificationModel notificationModel , String user_id , String room_id ) {
        NotificationModel  model  = notificationModel  ;
        model.setAction("notification");
        model.setUser_id(user_id);
        Call<JSONObject> jsonObjectCall  = Injector.FcmApi().send_notification(
                new FCM_Notfication_OBJ(
                        "/topics/"+"user_notification"
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
