package com.titfer.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Activties.Service.CallbackWithRetry;
import com.titfer.Activties.Service.Injector;
import com.titfer.Activties.Service.onRequestFailure;
import com.titfer.FCM.FCM_Notfication_OBJ;
import com.titfer.Fragments.Events.EventsDetailsFragment;
import com.titfer.Models.CartModel;
import com.titfer.Models.EventsModel;
import com.titfer.Models.NotificationModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.CartAdapter;
import com.titfer.adapter.EventsAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.CartListener;
import com.titfer.intefraces.HolderListener;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;

public class CartListFragment extends Fragment implements CartListener {

    FirebaseDatabase database  ;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @BindView(R.id.no_search_results)
    TextView no_news;

    View res_layout ;
    ArrayList<CartModel> cartModels = new ArrayList<>();
    private CartAdapter adapter;
    public CartListFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout= inflater.inflate(R.layout.fragment_cart, container, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                mPageEndOffset = savedInstanceState.getInt("mPageEndOffset");
                cartModels = (ArrayList<CartModel>) savedInstanceState.getSerializable("cartModels");
            }else {
              get_data();
            }
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new CartAdapter(cartModels,(AppCompatActivity) getActivity() , this);
        LinearLayoutManager layoutManager ;
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                get_data(cartModels.get(cartModels.size()-1).getId());
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mPageEndOffset", mPageEndOffset );
        outState.putSerializable("cartModels" , cartModels);
    }

    int  mPageEndOffset = 0;
    int mPageLimit = 2;

    private int getIndexForKey(String key) {
        int index = 0;
        for (CartModel cartModel : cartModels) {
            if (cartModel.getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_data( ) {
        showLoading(true);


        usersdb1 = database.getReference("Cart/"+AppContoller.getInstance().getPrefManager().getUser().getId()).limitToLast(mPageLimit);
        usersdb1.keepSynced(true);





        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Log.e("home" , " cart" + dataSnapshot.toString()) ;
                        if (!dataSnapshot.getChildren().iterator().hasNext()){
                            showLoading(false);
                            adapter.notifyDataSetChanged();
                            if (cartModels.size() == 0 ){
                                no_news.setVisibility(View.VISIBLE);

                            }
                            return;
                        }

                        no_news.setVisibility(View.GONE);

                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            final CartModel cartModel = child.getValue(CartModel.class);

                            int pos = getIndexForKey(cartModel.getId());
                            if (pos == -1) {
                               if (cartModel.getState().equals("finished") || cartModel.getState().equals("confirmed")){
                                   showLoading(false);
                                   adapter.showLoading(false);
                                   cartModels.add(cartModel);
                                   adapter.notifyDataSetChanged();
                               }
                            } else {
                                if (!cartModel.getState().equals("finished") && !cartModel.getState().equals("confirmed")){
                                    cartModels.remove(pos);
                                    adapter.notifyDataSetChanged();
                                    if (cartModels.size() ==0)
                                        no_news.setVisibility(View.VISIBLE);
                                }else {
                                    cartModels.set(pos,cartModel);
                                    adapter.notifyItemChanged(pos);
                                }

                            }
                        }
                        showLoading(false);


                    }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;


    }



    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


    private void showLoading(final boolean show) {
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

    Query usersdb1 ;
    Query usersdb2 ;
    Query albums_db ;

    void  get_data(String lastSeenID ) {



        usersdb2 = database.getReference("Cart/"+AppContoller.getInstance().getPrefManager().getUser().getId()).startAt(lastSeenID).limitToLast(mPageLimit);
        usersdb2.keepSynced(true);

        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Events page" , dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    adapter.showLoading(false);
                    adapter.notifyDataSetChanged();
                    return;
                }

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                                final CartModel cartModel = child.getValue(CartModel.class);

                                        int pos = getIndexForKey(cartModel.getId());
                                        if (pos == -1) {
                                            showLoading(false);
                                            adapter.showLoading(false);
                                            cartModels.add(cartModel);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            cartModels.set(pos,cartModel);
                                            adapter.notifyItemChanged(pos);
                                        }
                            }

             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;





    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        database.purgeOutstandingWrites();

    }




    @Override
    public void onHolderClicked(View view, CartModel child) {

    }

    @Override
    public void onConfirm(View view, final  CartModel child) {
            showLoading(true);
        child.setState("confirmed");
        database.getReference("Cart/"+ AppContoller.getInstance().getPrefManager().getUser().getId())
        .child(child.getId())
        .setValue(child).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                add_notfication(child);
                showLoading(false);
            }
        });
    }

    @Override
    public void onCancel(View view, final CartModel child) {
        showLoading(true);
        child.setState("canceled");
        database.getReference("Cart/"+ AppContoller.getInstance().getPrefManager().getUser().getId())
                .child(child.getId())
                .setValue(child).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                add_notfication(child);
                showLoading(false);
            }
        });
    }



    void add_notfication(CartModel cartModel){
        String name  = AppContoller.getInstance().getPrefManager().getUser().getFirstName() +" " + AppContoller.getInstance().getPrefManager().getUser().getLastName() ;
        String message = name +" has "+cartModel.getState()+" your offer for "+cartModel.getTitle() ;
        String message_admin = name +" has "+cartModel.getState()+" an offer for item "+cartModel.getTitle()  + " from designer " +cartModel.getDesigner_name() ;

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
                        "/topics/"+room_id
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


}
