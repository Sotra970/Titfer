package com.titfer.Fragments.Album;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
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
import com.titfer.Activties.Upload.ImageUploadActivity;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.adapter.AlbumDetailsAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.Add_album_listener;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumDetailsFragment extends Fragment implements HolderListener , Add_album_listener {


    @BindView(R.id.albums)
    RecyclerView albumsItemsRecycler;


    AlbumDetailsAdapter albumAdapter ;


    public AlbumDetailsFragment() {
        // Required empty public constructor
    }

    ArrayList<AlbumModel> albums_list = new ArrayList<>() ;


    AlbumModel albumModel ;
    FirebaseDatabase database  ;
    View res_layout  ;

    UserModel userModel;

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
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


    @OnClick(R.id.bar_back)
    void bar_back(){
        getActivity().onBackPressed();
    }


    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;

    @BindView(R.id.no_search_results)
    TextView no_search_results ;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



            if (res_layout ==null){
                database = FirebaseDatabase.getInstance(Constants.Ref);
                res_layout =inflater.inflate(R.layout.fragment_album_details, container, false);
                ButterKnife.bind(this , res_layout) ;

                if (savedInstanceState !=null){
                    albumModel = (AlbumModel) savedInstanceState.getSerializable("itemModel");
                    userModel = (UserModel) savedInstanceState.getSerializable("userModel");
                    isMine = savedInstanceState.getBoolean("isMine");
                    from_tab = savedInstanceState.getString("from");
                    albums_list = (ArrayList<AlbumModel>) savedInstanceState.getSerializable("albums_list");

                }else {
                    if (AppContoller.getInstance().getPrefManager().getUser().getId().equals(userModel.getId())){
                        isMine = true ;
                    }
                    get_data();
                }

                if (AppContoller.getInstance().getPrefManager().getUser().getId().equals(userModel.getId())){
                    isMine = true ;
                }

                bar_title.setText(albumModel.getTitle());
                recycler_setup();

            }

        return  res_layout ;
    }

    void recycler_setup (){
        albumAdapter = new AlbumDetailsAdapter(albums_list , getContext() , this , this , isMine) ;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3) ;
        albumsItemsRecycler.setLayoutManager(gridLayoutManager);
        albumsItemsRecycler.setAdapter(albumAdapter);

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

//    void  getLikesOnce(AlbumModel itemModel , final is_liked is_liked){
//        Query usersdb = database.getReference("ItemsLikes/"+itemModel.getId());
//        usersdb.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.e("profile_dataSnapshot_ad" , "likes " + dataSnapshot.toString()) ;
//                if (dataSnapshot.getChildren() == null){
//                    is_liked.onFinish(false);
//                }else {
//                    boolean fav  = false;
//                    for (DataSnapshot child : dataSnapshot.getChildren()) {
//                        fav =    child.getKey()== AppContoller.getInstance().getPrefManager().getUserModel().getId() ? true : false ;
//                    }
//                    is_liked.onFinish(fav);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//
//
//    }
//
    void  getLikes(AlbumModel itemModel , final is_liked is_liked){
        Query usersdb = database.getReference("ItemsLikes/"+itemModel.getId()+"/"+AppContoller.getInstance().getPrefManager().getUser().getId());
        usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    is_liked.onFinish(false);
                }else {

                    is_liked.onFinish(true);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    DatabaseReference  usersdb ;
    void  get_data( ) {
        albums_list.clear();
        if (albumAdapter!=null)
        albumAdapter.notifyDataSetChanged();
        showloading(true);

          usersdb = database.getReference("AlbumsItems/"+albumModel.getId());

        usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildren() == null && !isMine) {
                    no_search_results.setVisibility(View.VISIBLE);
                    showloading(false);
                    return;
                } else {
                    no_search_results.setVisibility(View.GONE);
                    albums_list.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            final AlbumModel child = dataSnapshot1.getValue(AlbumModel.class);

                            getLikes(child, new is_liked() {
                                @Override
                                public void onFinish(boolean fav) {

                                    try {
                                        Log.e("album_ch", dataSnapshot.toString() + "fav : " +fav);
                                        child.setLiked(fav);
                                        int pos = getIndexForKey(child.getId());
                                        if (pos != -1)
                                            albums_list.set(pos, child);
                                        else
                                            albums_list.add(child);
                                        albumAdapter.notifyDataSetChanged();
                                    } catch (Exception e) {
                                    }


                                }
                            });

                        } else {
                            Log.e("album_rm", dataSnapshot1.toString());
                            try {

                                int pos = getIndexForKey(dataSnapshot1.getKey());
                                if (pos != -1)
                                    albums_list.remove(pos);
                                albumAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                            }
                        }

                    }
                    showloading(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;









    }

    @Override
    public void onHolderClicked(View view, Object child) {

        ItemDetailsFragment searchDesignersListFragment = new ItemDetailsFragment() ;
        searchDesignersListFragment.setUserModel(userModel);
        searchDesignersListFragment.setAlbumModel(albumModel);
        searchDesignersListFragment.setItemModel((AlbumModel) child);
        searchDesignersListFragment.setFrom_tab(from_tab);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
       if (from_tab.contains("profile"))
        fragmentTransaction.replace(R.id.profile_content_frame, searchDesignersListFragment);
       if (from_tab.contains("search"))
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
       else if (from_tab.contains("main"))
           fragmentTransaction.replace(R.id.home_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack(from_tab+"_album_details");
        fragmentTransaction.commit();

    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }

    @Override
    public void onHolderClicked(View view) {
        Intent i = new Intent(getContext(), ImageUploadActivity.class);
        i.putExtra("extra",albumModel);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("itemModel", albumModel );
        outState.putSerializable("userModel", userModel);
        outState.putBoolean("isMine", isMine);
        outState.putString("from", from_tab);
        outState.putSerializable("albums_list" , albums_list);
    }


    public  interface is_liked {
        void onFinish(boolean fav);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
