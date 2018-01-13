package com.titfer.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.titfer.Fragments.Profile.ProfileFragment;
import com.titfer.Models.ReportModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.ProplemsAdapter;
import com.titfer.app.Constants;
import com.titfer.intefraces.ReportHolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReporstListFragment extends Fragment implements ReportHolderListener {

    FirebaseDatabase database  ;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.bar_title)
    TextView bar_title ;


    @BindView(R.id.no_search_results)
    TextView no_search_results ;

    View res_layout ;
    ArrayList<ReportModel> designerSearchModels = new ArrayList<>();
    private ProplemsAdapter adapter;
    public ReporstListFragment() {
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
                designerSearchModels = (ArrayList<ReportModel>) savedInstanceState.getSerializable("designerSearchModels");
            }else {
              get_data();
            }
            bar_title.setText("reports");
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new ProplemsAdapter(designerSearchModels ,(AppCompatActivity) getActivity() , this);
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
        for (ReportModel designerSearchModel : designerSearchModels) {
            if (designerSearchModel.getUserModel().getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_data( ) {
        showLoading(true);



         usersdb1 = database.getReference("Reports").limitToLast(mPageLimit);
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

                    final ReportModel reportModel = child.getValue(ReportModel.class);
                    reportModel.setKey(child.getKey());

                    database.getReference().child("Users/"+reportModel.getUser_id()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                           final UserModel userModel = dataSnapshot.getValue(UserModel.class);


                            reportModel.setUserModel(userModel);

                                    int pos = getIndexForKey(userModel.getId());
                                    if (pos == -1) {
                                        showLoading(false);
                                        adapter.showLoading(false);
                                        designerSearchModels.add(reportModel);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        designerSearchModels.set(pos, reportModel);
                                        adapter.notifyItemChanged(pos);
                                    }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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



        usersdb2 = database.getReference("Reports").startAt(lastSeenID).limitToLast(mPageLimit);
        usersdb2.keepSynced(true);

        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    adapter.showLoading(false);
                    adapter.notifyDataSetChanged();
                    return;
                }

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        final ReportModel reportModel = child.getValue(ReportModel.class);
                        reportModel.setKey(child.getKey());

                        database.getReference().child("Users/"+reportModel.getUser_id()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final UserModel userModel = dataSnapshot.getValue(UserModel.class);


                                reportModel.setUserModel(userModel);

                                int pos = getIndexForKey(userModel.getId());
                                if (pos == -1) {
                                    showLoading(false);
                                    adapter.showLoading(false);
                                    designerSearchModels.add(reportModel);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    designerSearchModels.set(pos, reportModel);
                                    adapter.notifyItemChanged(pos);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
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
    public void onHolderClicked(View view, Object child) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setUserModel((UserModel) child);
        profileFragment.setFrom_tab("search");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.proplems_container, profileFragment
        );
        fragmentTransaction.addToBackStack("search_designers");
        fragmentTransaction.commit();
    }

    @Override
    public void onHolderProplemClicked(String title) {
        new AlertDialog.Builder(getActivity())
                .setMessage(title)
                .setTitle("Proplem")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();

    }

}
