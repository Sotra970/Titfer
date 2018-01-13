package com.titfer.Activties.cahting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Activties.Insert.Add2CartActivity;
import com.titfer.Activties.Service.CallbackWithRetry;
import com.titfer.Activties.Service.Injector;
import com.titfer.Activties.Service.onRequestFailure;
import com.titfer.FCM.FCM_CHAT_MESSAGE_OBJ;
import com.titfer.Models.MessageModel;
import com.titfer.R;
import com.titfer.adapter.MessagesAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.Constants;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MessageRoomDetailsActivity extends AppCompatActivity  {
    SwipeRefreshLayout swipeRefresh ;
    RecyclerView course_member_list;
    List<Mess_tabel> messageModels ;
    private MessagesAdapter adapter;
    EditText message_input ;
    View send_btt ;
    private static String today;
    String room_id ;
    BroadcastReceiver broadcastReceiver ;


    FirebaseDatabase database  ;

    DatabaseReference messages_ref;
    View noRooms  ;
    @BindView(R.id.chat_bg)
    ImageView chat_bg ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room_details);
        ButterKnife.bind(this);


//        Glide.with(getApplicationContext())
//                .load(R.drawable.chatbg)
//                .apply(new RequestOptions().centerCrop())
//                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
//                .transition(new DrawableTransitionOptions().crossFade())
//                .into(chat_bg);


        room_id  =  getIntent().getExtras().getString("room_id") ;
        if (room_id == null)
            finish();



       try {
           Room_tabel room_tabel = new Room_tabel().get(room_id) ;
           String my_key = AppContoller.getInstance().getPrefManager().getUser().getId() ;
           if (room_tabel.type.equals("prod") && room_tabel.designer_id.equalsIgnoreCase(my_key)  && !room_tabel.consumer_id.equals(my_key))
               toolbar_menu.setVisibility(View.VISIBLE);
           else
               toolbar_menu.setVisibility(View.GONE);
       }catch (Exception e){
           e.printStackTrace();
       }
        setup_menu();

        noRooms = findViewById(R.id.no_rooms);
        database = FirebaseDatabase.getInstance(Constants.Ref);
        messages_ref = database.getReference("Messages/"+room_id) ;
        messages_ref.keepSynced(true);
        get_rooms() ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        try{
            ( (TextView)findViewById(R.id.mess_room_name)).setText(new Room_tabel().get(room_id).name);
        }catch (Exception e){
            e.printStackTrace();
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        course_member_list = (RecyclerView) findViewById(R.id.message_recycler_view);
        message_input = (EditText) findViewById(R.id.message_input);

        send_btt = findViewById(R.id.send_btt);

        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        setupRefreshLayout();
        recycleSetUP();

        send_btt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Mess_tabel  mess_tabel =  new Mess_tabel() ;
                mess_tabel.message= (message_input.getText().toString());
                mess_tabel.room_id= room_id;
                mess_tabel.user_id = (AppContoller.getInstance().getPrefManager().getUser().getId());
                mess_tabel.timeStamp = currentDateFormat() ;
                mess_tabel.dir = 2;
                message_input.setText("");
                send_message(mess_tabel) ;
            }
        });

         broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("broadcast", "receive broadcast");
                Log.e("broadcast", intent.getAction() + "");
                // checking for type intent filter
                if (intent.getAction().equals(Config.REFRESH_MESSAGES)) {
                    Log.e("broadcast", Config.REFRESH_MESSAGES);
//                    new Room_tabel().update_count(userModel.getId());
                    if (room_id.equals(intent.getExtras().getString("room_id")))
                    getData();

                }


            }
        };


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








    public  void get_rooms() {

        messages_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    noRooms.setVisibility(View.VISIBLE);
                    return;
                } else {
                    noRooms.setVisibility(View.GONE);
                }
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    Log.e("message_ac" ,"get_room_data" + " " + message.getValue().toString() );
                    MessageModel messageModel = message.getValue(MessageModel.class) ;
                    Log.e("message_ac" ,"get_room_data" + " " + messageModel.message);
                    Mess_tabel mess_tabel = new Mess_tabel() ;
                    mess_tabel.mess_id = messageModel.mess_id  ;
                    mess_tabel.user_name = messageModel.user_name  ;
                    mess_tabel.room_id = messageModel.room_id  ;
                    mess_tabel.message = messageModel.message ;
                    mess_tabel.user_id = messageModel.user_id ;
                    mess_tabel.timeStamp = messageModel.timeStamp;
                    if (messageModel.user_id.equals(AppContoller.getInstance().getPrefManager().getUser().getId()))
                        mess_tabel.dir = 2 ;
                    else mess_tabel.dir = 1 ;

                    if (!mess_tabel.check(mess_tabel.mess_id)){
                        mess_tabel.save();

                    }

                    adapter.insertData(mess_tabel);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public  void send_message(final Mess_tabel mess_tabel){
        String key =  messages_ref.push().getKey() ;
        final MessageModel messageModel = new MessageModel() ;
        messageModel.mess_id = key ;
        mess_tabel.mess_id = key ;
        messageModel.message = mess_tabel.message ;
        messageModel.room_id =room_id ;
        mess_tabel.room_id = room_id ;
        messageModel.user_id = AppContoller.getInstance().getPrefManager().getUser().getId() ;
        mess_tabel.user_id = AppContoller.getInstance().getPrefManager().getUser().getId() ;
        messageModel.user_name = AppContoller.getInstance().getPrefManager().getUser().getFirstName() + AppContoller.getInstance().getPrefManager().getUser().getLastName() ;
        mess_tabel.user_name = AppContoller.getInstance().getPrefManager().getUser().getFirstName() + AppContoller.getInstance().getPrefManager().getUser().getLastName() ;
        messageModel.timeStamp = mess_tabel.timeStamp;

        adapter.insertData(mess_tabel);

        messages_ref.child(key).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                mess_tabel.save() ;
                send_notfication(messageModel);
            }
        }) ;

//
    }

    private void send_notfication(MessageModel mess_tabel) {
        MessageModel messageModel = mess_tabel  ;
        messageModel.action = "message" ;
        Call<JSONObject> jsonObjectCall  = Injector.FcmApi().send_chat_message(
                new FCM_CHAT_MESSAGE_OBJ(
                        "/topics/"+room_id
                        ,mess_tabel
                )
        );
        jsonObjectCall.enqueue(new CallbackWithRetry<JSONObject>(5, 3000, jsonObjectCall, new onRequestFailure() {
            @Override
            public void onFailure() {
                Log.e("send_chat_message" , "fail");
            }
        }) {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                Log.e("send_chat_message" , "sucess " + response.body().toString());
            }
        });

    }


    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }
    public static String currentDateFormat(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String  currentTimeStamp = dateFormat.format(new Date());
        return currentTimeStamp;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return  true ;
    }

    private void setupRefreshLayout(){
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_intersted_courses_data();
            }
        });
    }

    private void endLoading() {
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            course_member_list.setAlpha(0);
            course_member_list.animate().alpha(1);
        }else {
            course_member_list.setVisibility(View.VISIBLE);
        }
    }

    void recycleSetUP() {

            messageModels = new ArrayList<>() ;
        adapter = new MessagesAdapter(this, messageModels );
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext() , LinearLayoutManager.VERTICAL , true);
        course_member_list.setLayoutManager(layoutManager);
        course_member_list.setAdapter(adapter);
        Log.e("members " , messageModels.size() +"");
        getData();


    }
    private void get_intersted_courses_data() {
        endLoading();
    }
    void getData(){
        int prev = messageModels.size();
            messageModels = new Mess_tabel().list_of_room_data(room_id) ;
        Log.e("messages_prev",messageModels.toString());
        for (int i=prev ; i<messageModels.size() ; i++){
            adapter.insertData(messageModels.get(i));
        }


    }

    protected void onResume() {
        super.onResume();
        Log.e("broadcast","register");

        // register new news feed  notification broadcast receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.REFRESH_MESSAGES));

        AppContoller.getInstance().getPrefManager().set_current_user(room_id);

    }



    @Override
    protected void onPause() {
        Log.e("broadcast","unregister");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        AppContoller.getInstance().getPrefManager().set_current_user("0");
        super.onPause();
    }









    @Override
    public void onBackPressed() {
       try{
           Intent intent = new Intent();
           intent.putExtra("room_id",getIntent().getExtras().getString("room_id")) ;
           intent.putExtra("position",getIntent().getExtras().getInt("position")) ;
           setResult(200,intent);
       }catch (Exception e){
           setResult(200,null);

       }
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);

    }
   @OnClick(R.id.bar_back)
    void back(){
       onBackPressed();
   }

//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("messageModels" , messageModels.toArray());
//    }



    @OnClick(R.id.toolbar_menu)
    public void showPopup() {

        popup.show();
    }


    @BindView(R.id.toolbar_menu)
    ImageView toolbar_menu;

    PopupMenu popup;

    void setup_menu(){

        popup = new PopupMenu(this,toolbar_menu ) ;
        MenuInflater inflater = popup.getMenuInflater();
        if (AppContoller.getInstance().getPrefManager().getUser().getType() == 1 )
            inflater.inflate(R.menu.cart_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.cart_action :
                        Intent intent = new Intent(getApplication() , Add2CartActivity.class);
                        intent.putExtra("room_id" , room_id) ;
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in , R.anim.fade_in);
                        break;


                }
                return false;
            }
        });
    }
}


