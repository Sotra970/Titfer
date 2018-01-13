package com.titfer.Fragments.home;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Fragments.Profile.ProfileFragment;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CatModel;
import com.titfer.Models.DesignerSearchModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.DesignerSearchAdapter;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BlogsDesignersListFragment extends Fragment implements HolderListener {

    CatModel catModel ;
    FirebaseDatabase database  ;
    public void setCatModel(CatModel catModel) {
        this.catModel = catModel;
    }


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.bar_title)
    TextView bar_title ;


    @BindView(R.id.no_search_results)
    TextView no_search_results ;

    View res_layout ;
    ArrayList<DesignerSearchModel> designerSearchModels = new ArrayList<>();
    private DesignerSearchAdapter adapter;
    public BlogsDesignersListFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.bar_back)
    void bar_back(){
        getActivity().onBackPressed();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout= inflater.inflate(R.layout.fragment_designers_search_list, null, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                catModel = (CatModel) savedInstanceState.getSerializable("cat");
                mPageEndOffset = savedInstanceState.getInt("mPageEndOffset");
                designerSearchModels = (ArrayList<DesignerSearchModel>) savedInstanceState.getSerializable("designerSearchModels");
            }else {
              get_data();
            }
            bar_title.setText(catModel.getName());
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new DesignerSearchAdapter(designerSearchModels ,(AppCompatActivity) getActivity() , this);
        LinearLayoutManager layoutManager ;
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                get_data(designerSearchModels.get(designerSearchModels.size()-1).getUserModel().getId());
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cat", catModel );
        outState.putInt("mPageEndOffset", mPageEndOffset );
        outState.putSerializable("designerSearchModels" , designerSearchModels);
    }

    int  mPageEndOffset = 0;
    int mPageLimit = 2;

    private int getIndexForKey(String key) {
        int index = 0;
        for (DesignerSearchModel designerSearchModel : designerSearchModels) {
            if (designerSearchModel.getUserModel().getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }

    void get_albums(UserModel userModel, final GetAlbums getAlbums ){
        final ArrayList<AlbumModel> albumModels = new ArrayList<>() ;
        albums_db = database.getReference("Albums/"+ userModel.getId()).limitToFirst(5);
        albums_db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()){
                    if (child.exists()){
                        albumModels.add(child.getValue(AlbumModel.class));
                    }
                }
                getAlbums.onFinish(albumModels);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    void  get_data( ) {
        showLoading(true);



         usersdb1 = database.getReference(catModel.getName()).limitToFirst(mPageLimit);
        usersdb1.keepSynced(true);





        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.e("search" , catModel.getName() + " " + dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    showLoading(false);
                    adapter.notifyDataSetChanged();
                    if (designerSearchModels.size() == 0 ){
                        no_search_results.setVisibility(View.VISIBLE);
                    }
                    return;
                }


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    database.getReference().child("Users/"+child.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.e("search child ", catModel.getName() + " " + dataSnapshot.toString());

                           final UserModel userModel = dataSnapshot.getValue(UserModel.class);

                            get_albums(userModel, new GetAlbums() {
                                @Override
                                public void onFinish(ArrayList<AlbumModel> albumModels) {
                                    int pos = getIndexForKey(userModel.getId());
                                    if (pos == -1) {
                                        showLoading(false);
                                        adapter.showLoading(false);
                                        designerSearchModels.add(new DesignerSearchModel(userModel, albumModels));
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        designerSearchModels.set(pos, new DesignerSearchModel(userModel, albumModels));
                                        adapter.notifyItemChanged(pos);
                                    }
                                }
                            });
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



        usersdb2 = database.getReference(catModel.getName()).startAt(lastSeenID).limitToFirst(mPageLimit);
        usersdb2.keepSynced(true);

        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("search page" , catModel.getName() + " " + dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    adapter.showLoading(false);
                    adapter.notifyDataSetChanged();
                    return;
                }

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        database.getReference().child("Users/"+child.getKey()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final UserModel userModel = dataSnapshot.getValue(UserModel.class);

                                get_albums(userModel, new GetAlbums() {
                                    @Override
                                    public void onFinish(ArrayList<AlbumModel> albumModels) {
                                        int pos = getIndexForKey(userModel.getId());
                                        if (pos == -1) {
                                            showLoading(false);
                                            adapter.showLoading(false);
                                            designerSearchModels.add(new DesignerSearchModel(userModel, albumModels));
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            designerSearchModels.set(pos, new DesignerSearchModel(userModel, albumModels));
                                            adapter.notifyItemChanged(pos);
                                        }
                                    }
                                });
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

    interface  GetAlbums{
        void onFinish(ArrayList<AlbumModel> albumModel);
    }


    @Override
    public void onHolderClicked(View view, Object child) {
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setUserModel((UserModel) child);
        profileFragment.setFrom_tab("main");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.home_content_frame, profileFragment
        );
        fragmentTransaction.addToBackStack("search_designers");
        fragmentTransaction.commit();
    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }

}
