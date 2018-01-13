package com.titfer.Fragments.search;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Fragments.Profile.ProfileFragment;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CatModel;
import com.titfer.Models.DesignerSearchModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.adapter.DesignerSearchAdapter;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchDesignersKeyWordListFragment extends Fragment implements HolderListener {

    CatModel catModel ;
    FirebaseDatabase database  ;
    private Query albums_db;

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
    public SearchDesignersKeyWordListFragment() {
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
            res_layout= inflater.inflate(R.layout.fragment_designers_search_list, container, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                catModel = (CatModel) savedInstanceState.getSerializable("cat");
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
        adapter.showLoading(false);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cat", catModel );
        outState.putSerializable("designerSearchModels" , designerSearchModels);
    }

    int mPageLimit = 10;

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

    boolean fnames = true , lnames = true  , bnames = true;

    void  get_data( ) {
        showLoading(true);
        String key_word_text = catModel.getName() ;
        fnames = true ;
        lnames = true  ;
        bnames = true;
        DatabaseReference users_ref = database.getReference("Users") ;

        // search for frist name
        users_ref.limitToFirst(20).orderByChild("firstName").startAt(key_word_text).endAt(key_word_text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//
                Log.e("search" , catModel.getName() + " " + dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    fnames = false ;
                    if (designerSearchModels.size() == 0 && !fnames && !lnames && !bnames){
                        no_search_results.setVisibility(View.VISIBLE);
                        showLoading(false);
                    }
                    return;
                }


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Log.e("search child ", " " + child.toString());

                    final UserModel userModel = child.getValue(UserModel.class);

                    get_albums(userModel, new GetAlbums() {
                        @Override
                        public void onFinish(ArrayList<AlbumModel> albumModel) {
                            int pos = getIndexForKey(userModel.getId());
                            if (pos == -1) {
                                showLoading( false);
                                designerSearchModels.add(new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyDataSetChanged();
                            } else {
                                designerSearchModels.set(pos, new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyItemChanged(pos);
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // search for last name
        users_ref.limitToFirst(20).orderByChild("lastName").startAt(key_word_text).endAt(key_word_text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//
                Log.e("search" , catModel.getName() + " " + dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    lnames = false ;
                    if (designerSearchModels.size() == 0 && !fnames && !lnames && !bnames){
                        no_search_results.setVisibility(View.VISIBLE);
                        showLoading(false);
                    }
                    return;
                }


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Log.e("search child ", " " + child.toString());

                    final UserModel userModel = child.getValue(UserModel.class);

                    get_albums(userModel, new GetAlbums() {
                        @Override
                        public void onFinish(ArrayList<AlbumModel> albumModel) {
                            int pos = getIndexForKey(userModel.getId());
                            if (pos == -1) {
                                showLoading( false);
                                designerSearchModels.add(new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyDataSetChanged();
                            } else {
                                designerSearchModels.set(pos, new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyItemChanged(pos);
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // search for brand name
        users_ref.limitToFirst(20).orderByChild("brandName").startAt(key_word_text).endAt(key_word_text).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

//
                Log.e("search" , catModel.getName() + " " + dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    bnames = false ;
                    if (designerSearchModels.size() == 0 && !fnames && !lnames && !bnames ){
                        no_search_results.setVisibility(View.VISIBLE);
                        showLoading(false);
                    }
                    return;
                }
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Log.e("search child ", " " + child.toString());

                    final UserModel userModel = child.getValue(UserModel.class);

                    get_albums(userModel, new GetAlbums() {
                        @Override
                        public void onFinish(ArrayList<AlbumModel> albumModel) {
                            int pos = getIndexForKey(userModel.getId());
                            if (pos == -1) {
                                showLoading( false);
                                designerSearchModels.add(new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyDataSetChanged();
                            } else {
                                designerSearchModels.set(pos, new DesignerSearchModel(userModel, albumModel));
                                adapter.notifyItemChanged(pos);
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }






    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


    private void showLoading(final boolean show) {
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
        profileFragment.setFrom_tab("search");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, profileFragment
        );
        fragmentTransaction.addToBackStack("search_designers");
        fragmentTransaction.commit();
    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }

}
