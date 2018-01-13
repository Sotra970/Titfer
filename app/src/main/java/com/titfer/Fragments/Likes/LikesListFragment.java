package com.titfer.Fragments.Likes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
import com.titfer.Fragments.Album.AlbumDetailsFragment;
import com.titfer.Fragments.Album.ItemDetailsFragment;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.RecomendationModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.ItemLikesAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikesListFragment extends Fragment implements HolderListener {

    FirebaseDatabase database  ;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.bar_back)
    View bar_back ;


    @BindView(R.id.no_search_results)
    TextView no_news;

    View res_layout ;
    ArrayList<RecomendationModel> newsModels = new ArrayList<>();
    private ItemLikesAdapter adapter;
    public LikesListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (res_layout == null){
            database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout= inflater.inflate(R.layout.fragment_likes_list, container, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                mPageEndOffset = savedInstanceState.getInt("mPageEndOffset");
                newsModels = (ArrayList<RecomendationModel>) savedInstanceState.getSerializable("newsModels");
            }else {
              get_data();
            }
            bar_back.setVisibility(View.GONE);
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new ItemLikesAdapter((AppCompatActivity) getActivity()  ,newsModels , this);
        GridLayoutManager layoutManager ;
        layoutManager = new GridLayoutManager(getContext() , 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                get_data(newsModels.get(newsModels.size()-1).getUserModel().getId());
            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mPageEndOffset", mPageEndOffset );
        outState.putSerializable("newsModels" , newsModels);
    }

    int  mPageEndOffset = 0;
    int mPageLimit = 2;

    private int getIndexForKey(String key) {
        int index = 0;
        for (RecomendationModel recomendationModel  : newsModels) {
            if (recomendationModel.getItemModel().getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_data( ) {
        showLoading(true);







        usersdb1 = database.getReference("UserLikes/"+AppContoller.getInstance().getPrefManager().getUser().getId()).limitToLast(mPageLimit);
        usersdb1.keepSynced(true);





        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try {
                    Log.e("home" , " Likes" + dataSnapshot.toString()) ;

                    if (!dataSnapshot.getChildren().iterator().hasNext()){
                        showLoading(false);
                        adapter.notifyDataSetChanged();
                        if (newsModels.size() == 0 ){
                            no_news.setVisibility(View.VISIBLE);
                        }
                        return;
                    }else {
                        no_news.setVisibility(View.GONE);
                    }



                    for (final DataSnapshot child : dataSnapshot.getChildren()){
                        final RecomendationModel recomendationModel = new RecomendationModel() ;
                        final String user_key = child.getKey() ;
                        final String albu_key = child.getChildren().iterator().next().getKey() ;
                        final String item_key = child.getChildren().iterator().next().getChildren().iterator().next().getKey() ;

                        Log.e("like_list" , "child" + child.toString()) ;
                        Log.e("like_list" , "user_key" +user_key) ;
                        Log.e("like_list" , "albu_key" +albu_key) ;
                        Log.e("like_list" , "item_key" +item_key) ;


                        database.getReference("Users").child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                                recomendationModel.setUserModel(userModel);
                                database.getReference("Albums").child(user_key).child(albu_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class) ;
                                        recomendationModel.setAlbumModel(albumModel);


                                        database.getReference("AlbumsItems").child(albu_key).child(item_key)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        final AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class);
                                                        if (albumModel ==null)
                                                            return;

                                                        getLikes(albumModel, new AlbumDetailsFragment.is_liked() {
                                                            @Override
                                                            public void onFinish(boolean fav) {
                                                                albumModel.setLiked(fav);
                                                                recomendationModel.setItemModel(albumModel);
                                                                int pos = getIndexForKey(recomendationModel.getItemModel().getId());
                                                                if (pos == -1) {
                                                                    showLoading(false);
//                                                                    adapter.showLoading(false);
                                                                    newsModels.add(recomendationModel);
                                                                    adapter.notifyDataSetChanged();
                                                                } else {
                                                                    newsModels.set(pos,recomendationModel);
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

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }catch (Exception e){
                    e.printStackTrace();
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



            usersdb2 = database.getReference("UserLikes/"+AppContoller.getInstance().getPrefManager().getUser().getId()).startAt(lastSeenID).limitToLast(mPageLimit);
        usersdb2.keepSynced(true);

//        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                try {
                    Log.e("home" , " Likes" + dataSnapshot.toString()) ;

                    if (!dataSnapshot.getChildren().iterator().hasNext()){
                        showLoading(false);
                        adapter.notifyDataSetChanged();
                        if (newsModels.size() == 0 ){
                            no_news.setVisibility(View.VISIBLE);
                        }
                        return;
                    }else {
                        no_news.setVisibility(View.GONE);
                    }



                    for (final DataSnapshot child : dataSnapshot.getChildren()){
                        final RecomendationModel recomendationModel = new RecomendationModel() ;
                        final String user_key = child.getKey() ;
                        final String albu_key = child.getChildren().iterator().next().getKey() ;
                        final String item_key = child.getChildren().iterator().next().getChildren().iterator().next().getKey() ;
                        database.getReference("Users").child(user_key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                                recomendationModel.setUserModel(userModel);
                                database.getReference("Albums").child(user_key).child(albu_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class) ;
                                        recomendationModel.setAlbumModel(albumModel);


                                        database.getReference("AlbumsItems").child(albu_key).child(item_key)
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        final AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class);


                                                        getLikes(albumModel, new AlbumDetailsFragment.is_liked() {
                                                            @Override
                                                            public void onFinish(boolean fav) {
                                                                albumModel.setLiked(fav);
                                                                recomendationModel.setItemModel(albumModel);
                                                                int pos = getIndexForKey(recomendationModel.getItemModel().getId());
                                                                if (pos == -1) {
                                                                    showLoading(false);
//                                                                    adapter.showLoading(false);
                                                                    newsModels.add(recomendationModel);
                                                                    adapter.notifyDataSetChanged();
                                                                } else {
                                                                    newsModels.set(pos,recomendationModel);
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

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                }catch (Exception e){
                    e.printStackTrace();
                }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;





    }



    void  getLikes(AlbumModel itemModel , final AlbumDetailsFragment.is_liked is_liked){
        Query usersdb = database.getReference("ItemsLikes/"+itemModel.getId()+"/"+ AppContoller.getInstance().getPrefManager().getUser().getId());
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




    @Override
    public void onDestroy() {
        super.onDestroy();
        database.purgeOutstandingWrites();

    }



    @Override
    public void onHolderClicked(View view, Object child) {

        String from_tab = "main" ;
        ItemDetailsFragment searchDesignersListFragment = new ItemDetailsFragment() ;
        searchDesignersListFragment.setUserModel(((RecomendationModel) child).getUserModel());
        searchDesignersListFragment.setAlbumModel(((RecomendationModel) child).getAlbumModel());
        searchDesignersListFragment.setItemModel(((RecomendationModel) child).getItemModel());
        searchDesignersListFragment.setFrom_tab(from_tab);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        fragmentTransaction.replace(R.id.fav_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("favs");
        fragmentTransaction.commit();

    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }

}
