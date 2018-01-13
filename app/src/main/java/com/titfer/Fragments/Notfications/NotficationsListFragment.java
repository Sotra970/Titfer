package com.titfer.Fragments.Notfications;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Models.NotificationModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.NotificationsAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NotficationsListFragment extends Fragment implements HolderListener {

    FirebaseDatabase database  ;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.bar_title)
    TextView bar_title ;


    @BindView(R.id.no_search_results)
    TextView no_search_results ;

    View res_layout ;
    ArrayList<NotificationModel> designerSearchModels = new ArrayList<>();
    private NotificationsAdapter adapter;
    public NotficationsListFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.bar_back)
    void bar_back(){
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout= inflater.inflate(R.layout.fragment_designers_search_list, container, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                mPageEndOffset = savedInstanceState.getInt("mPageEndOffset");
                designerSearchModels = (ArrayList<NotificationModel>) savedInstanceState.getSerializable("designerSearchModels");
            }else {
              get_data();
            }
            bar_title.setText("Notifications");
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new NotificationsAdapter(designerSearchModels ,(AppCompatActivity) getActivity() , this);
        LinearLayoutManager layoutManager ;
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                get_data(designerSearchModels.get(designerSearchModels.size()-1).getKey());
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mPageEndOffset", mPageEndOffset );
        outState.putSerializable("designerSearchModels" , designerSearchModels);
    }

    int  mPageEndOffset = 0;
    int mPageLimit = 2;

    private int getIndexForKey(String key) {
        int index = 0;
        for (NotificationModel designerSearchModel : designerSearchModels) {
            if (designerSearchModel.getKey().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_data( ) {
        showLoading(true);



         usersdb1 = database.getReference("Notification/"+ AppContoller.getInstance().getPrefManager().getUser().getId()).limitToLast(mPageLimit);
        usersdb1.keepSynced(true);





        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    showLoading(false);
                    adapter.notifyDataSetChanged();
                    if (designerSearchModels.size() == 0 ){
                        no_search_results.setVisibility(View.VISIBLE);
                    }
                    return;
                }


                for (final DataSnapshot child : dataSnapshot.getChildren()) {

                    final NotificationModel notificationModel = child.getValue(NotificationModel.class);
                    notificationModel.setKey(child.getKey());

                    int pos = getIndexForKey(notificationModel.getKey());

                    if (pos == -1) {
                        showLoading(false);
                        adapter.showLoading(false);
                        designerSearchModels.add(notificationModel);
                        adapter.notifyDataSetChanged();
                    } else {
                        designerSearchModels.set(pos, notificationModel);
                        adapter.notifyItemChanged(pos);
                    }

                }
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



        usersdb2 = database.getReference("Notification/"+ AppContoller.getInstance().getPrefManager().getUser().getId()).startAt(lastSeenID).limitToLast(mPageLimit);
        usersdb2.keepSynced(true);

        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()) {
                    adapter.showLoading(false);
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    final NotificationModel notificationModel = child.getValue(NotificationModel.class);
                    notificationModel.setKey(child.getKey());

                    int pos = getIndexForKey(notificationModel.getKey());

                    if (pos == -1) {
                        showLoading(false);
                        adapter.showLoading(false);
                        designerSearchModels.add(notificationModel);
                        adapter.notifyDataSetChanged();
                    } else {
                        designerSearchModels.set(pos, notificationModel);
                        adapter.notifyItemChanged(pos);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        database.purgeOutstandingWrites();

    }




    @Override
    public void onHolderClicked(View view, Object child) {
        new AlertDialog.Builder(getActivity())
                .setMessage(((NotificationModel) child ).getMessage())
                .setTitle("Notification")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();

    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }


}
