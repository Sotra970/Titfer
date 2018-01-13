package com.titfer.Fragments.Chat;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.titfer.Activties.cahting.MessageRoomDetailsActivity;
import com.titfer.Models.Room_model;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.RecyclerViewTouchHelper;
import com.titfer.adapter.MessageRoomAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.Constants;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;

import java.util.List;


public class ProdMessegesTabFragment extends Fragment {
    View res_layout ;
    SwipeRefreshLayout swipeRefresh ;
    RecyclerView course_member_list;
    List<Room_tabel> course_member_data ;
    private MessageRoomAdapter adapter;
    private BroadcastReceiver broadcastReceiver;

    public ProdMessegesTabFragment() {
        // Required empty public constructor
    }


    FirebaseDatabase database  ;

    DatabaseReference prod_rooms ;
    View noRooms  ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

            if (res_layout == null) {

                res_layout = inflater.inflate(R.layout.fragment_messeges_tab, container, false);
                noRooms = res_layout.findViewById(R.id.no_rooms);
                database = FirebaseDatabase.getInstance(Constants.Ref);
                prod_rooms = database.getReference("ProdChat/"+ AppContoller.getInstance().getPrefManager().getUser().getId()) ;

                setupRefreshLayout();

                course_member_data = new Room_tabel().data("prod") ;

                course_member_list = (RecyclerView) res_layout.findViewById(R.id.messegs_recycler_view);
                setupRefreshLayout();
                recycleSetUP();
                get_rooms() ;


                broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.e("broadcast", "receive broadcast");
                        Log.e("broadcast", intent.getAction() + "");
                        Log.e("broadcast", intent.getAction() + "");
                        // checking for type intent filter
                        if (intent.getAction().equals(Config.UPDATE_USER_CHAT_ICON)) {
                            Log.d("admin rooms broadcast", Config.UPDATE_USER_CHAT_ICON  );
                            addNotfication(intent.getExtras().getString("room_id"));
                        }

                    }
                } ;
            
            
            }   
        return res_layout;
    }



    private void setupRefreshLayout(){
        swipeRefresh = (SwipeRefreshLayout) res_layout.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.colorAccent));
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
            course_member_list.setAlpha(0f);
            course_member_list.animate().alpha(1f);
        }else {
            course_member_list.setVisibility(View.VISIBLE);
        }
    }

    void recycleSetUP() {
        adapter = new MessageRoomAdapter(getActivity(), course_member_data);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        course_member_list.setLayoutManager(layoutManager);
        course_member_list.setAdapter(adapter);
        Log.e("members " , course_member_data.size() +"");


        course_member_list.addOnItemTouchListener(new RecyclerViewTouchHelper(getContext(), course_member_list, new RecyclerViewTouchHelper.recyclerViewTouchListner() {
            @Override
            public void onclick(View child, int postion) {
                Room_tabel current =  adapter.getItem(postion);
                startMessDetails(child.findViewById(R.id.it_img),current.room_id , postion);
                clearNotfication(current.room_id,postion);
            }

            @Override
            public void onLongClick(View child, int postion) {

            }
        }));


    }
    private void get_intersted_courses_data() {
//        course_member_data = new Room_tabel().data() ;
        get_rooms();

    }



    void startMessDetails( View sharedView , String  room , int postion){
        Intent intent = new Intent(getContext(),MessageRoomDetailsActivity.class);
        intent.putExtra("room_id",room) ;
        intent.putExtra("position",postion) ;
        ActivityOptions options = null;
            startActivityForResult(intent, Config.MesaageDetailActivity);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

    }

    interface  FetchRoomData{
        void onFinish(String room_key);
    }



    interface  FetchUSer{
        void onFinish(UserModel userModel);
    }
    void getRoomID(String userKey , final FetchRoomData fetchRoomData){
        prod_rooms.child(userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                }
                else fetchRoomData.onFinish("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void getUser(String userKey , final FetchUSer fetchUSer){
        database.getReference("Users/"+userKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("ProdMessagesFrag" , "getRooms fetchUSer"+dataSnapshot);
                if (dataSnapshot.exists()){
                    fetchUSer.onFinish(dataSnapshot.getValue(UserModel.class));
                }else {
                    fetchUSer.onFinish(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (Room_tabel room_tabel : course_member_data) {
            if (room_tabel.room_id.equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }



    public  void get_rooms(){

        prod_rooms.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("ProdMessagesFrag" , "getRooms"+dataSnapshot);
                if (dataSnapshot.getChildrenCount() == 0 ){
                    noRooms.setVisibility(View.VISIBLE);
                    return;
                }else {
                    noRooms.setVisibility(View.GONE);
                }

                for (DataSnapshot item_key : dataSnapshot.getChildren()){
                    Log.e("ProdMessagesFrag" , "getRooms item_key"+item_key);
                    if (item_key.getChildrenCount() == 0 )
                        return;

                    for (DataSnapshot userKey : item_key.getChildren() ) {
                        Log.e("ProdMessagesFrag" , "getRooms userKey"+userKey);
                        final String userID = userKey.getKey() ;
                        final Room_tabel room_tabel = new Room_tabel() ;
                        final Room_model room_model = userKey.getChildren().iterator().next().getValue(Room_model.class) ;
                                room_tabel.room_id = room_model.room_id ;
                                room_tabel.consumer_id = room_model.consumer_key ;
                                room_tabel.designer_id = room_model.designer_key ;
                                getUser(userID, new FetchUSer() {
                                    @Override
                                    public void onFinish(UserModel userModel) {
                                        room_tabel.img = userModel.getProfilePic() ;
                                        room_tabel.name = userModel.getFirstName()+" " + userModel.getLastName();
                                        room_tabel.type = "prod";

                                        if (!room_tabel.check(room_tabel.room_id)){
                                            room_tabel.save();
                                            FirebaseMessaging.getInstance().subscribeToTopic(room_tabel.room_id);
                                        }
                                        if (getIndexForKey(room_tabel.room_id) == -1){
                                            course_member_data.add(room_tabel);
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                });


                            }
                    }
                endLoading();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




//

    }


    void addNotfication(String room_id){
        int postion =0 ;
        for(int i=0 ;i<course_member_data.size() ;i++)
        {
            if (course_member_data.get(i).room_id.equals(room_id))
            {
                postion=i;
                break;
            }
        }

        try{
            TextView chat_notifty = (TextView) course_member_list.getChildAt(postion).findViewById(R.id.chat_notify) ;

            TextView message = (TextView) course_member_list.getChildAt(postion).findViewById(R.id.message) ;


            int current =   new Room_tabel().unseenCount(room_id) ;
            Log.e("notfications ", "count :   "+current);



            Mess_tabel last_mess =  new Mess_tabel().last_mess(room_id);

                message.setText(last_mess.message);
            if (current==0)
                chat_notifty.setVisibility(View.GONE);
            else{
                chat_notifty.setVisibility(View.VISIBLE);
                chat_notifty.setText(current+"");
            }


        }catch (Exception e){}

    }
    void clearNotfication(String room_id , int postion) {
        TextView chat_notifty = (TextView) course_member_list.getChildAt(postion).findViewById(R.id.chat_notify) ;
        chat_notifty.setVisibility(View.GONE);
        new Room_tabel().clear(room_id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.MesaageDetailActivity)
            try{
                clearNotfication(data.getExtras().getString("room_id") ,data.getExtras().getInt("position") );
                adapter.notifyItemChanged(data.getExtras().getInt("position"));
            }catch (Exception e){}

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("broadcast","register");

        // register new news feed  notification broadcast receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.UPDATE_USER_CHAT_ICON));

    }





}


