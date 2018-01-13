package com.titfer.Fragments.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.titfer.Fragments.Album.ItemDetailsFragment;
import com.titfer.Fragments.Events.EventsListFragment;
import com.titfer.Fragments.News.NewsListFragment;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CatModel;
import com.titfer.Models.EventsModel;
import com.titfer.Models.NewsModel;
import com.titfer.Models.RecomendationModel;
import com.titfer.Models.UserModel;
import com.titfer.R;
import com.titfer.adapter.RecommendedAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HolderListener {



    public HomeFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.blogs_img)
    ImageView blogs_img  ;

    @BindView(R.id.events_icon)
    ImageView events_icon  ;

    @BindView(R.id.news_img)
    ImageView news_img  ;


    @BindView(R.id.add_space_1)
    ImageView space1  ;


    @BindView(R.id.add_space_2)
    ImageView space2  ;


    @BindView(R.id.blogs_txt)
    View blogs_txt  ;

    @BindView(R.id.events_txt)
    View events_txt  ;

    @BindView(R.id.news_txt)
    View news_txt  ;

    @BindView(R.id.no_recom)
    TextView no_recom  ;

    View res_layout ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){
            res_layout = inflater.inflate(R.layout.fragment_home, null, false);
            database = FirebaseDatabase.getInstance(Constants.Ref);
            ButterKnife.bind(this , res_layout) ;
//            if (savedInstanceState == null)
                get_rec_dataa() ;
//            else
//                recommendedArrayList = (ArrayList<RecomendationModel>) savedInstanceState.getSerializable("recommendedArrayList");

            recomenSetup();
            get_news();
            get_events();

            database.getReference("AdSpace1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Glide.with(getActivity())
                                .asDrawable()
                                .load(dataSnapshot.getValue(String.class))
                                .transition( new DrawableTransitionOptions().crossFade())
                                .thumbnail(0.5f)
                                .apply(new RequestOptions().fitCenter())
                                .apply(new RequestOptions().centerCrop())
                                .into(space1) ;
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



            database.getReference("AdSpace2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                  try{
                      Glide.with(getActivity())
                              .asDrawable()
                              .load(dataSnapshot.getValue(String.class))
                              .transition( new DrawableTransitionOptions().crossFade())
                              .thumbnail(0.5f)
                              .apply(new RequestOptions().centerCrop())
                              .into(space2) ;
                  }catch (Exception e){}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        return  res_layout ;
    }





    @BindView(R.id.recommended_circles_recyclerview)
    RecyclerView recommendedRecyclerView;
    private ArrayList<RecomendationModel> recommendedArrayList = new ArrayList<>();


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("recommendedArrayList" , recommendedArrayList);
    }


    RecommendedAdapter recommendedAdapter;
    void recomenSetup(){
        recommendedAdapter = new RecommendedAdapter(getActivity() ,recommendedArrayList,this);
        recommendedRecyclerView.setLayoutManager(new GridLayoutManager(getActivity() , 3));
        recommendedRecyclerView.setAdapter(recommendedAdapter);
    }

    FirebaseDatabase database  ;



    void get_rec_data(final  RecomendationModel recomendationModel ){

        database.getReference("Users").child(recomendationModel.getUserModel().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserModel userModel = dataSnapshot.getValue(UserModel.class) ;
                recomendationModel.setUserModel(userModel);
                get_rec_album(recomendationModel, new onDbVaFinish() {
                    @Override
                    public void onfinish(Object object) {
                        recomendationModel.setAlbumModel( ((RecomendationModel) object).getAlbumModel()) ;
                        get_rec_item(recomendationModel, new onDbVaFinish() {
                            @Override
                            public void onfinish(Object object) {
                                recomendationModel.setItemModel( ((RecomendationModel) object).getItemModel()); ;
                               if (getIndexForRecKey(recomendationModel.getItemModel().getId()) == -1){
                                   recommendedArrayList.add(recomendationModel) ;
                                   recommendedAdapter.notifyDataSetChanged();
                               }
                            }
                        });
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void get_rec_album( final  RecomendationModel recomendationModel    , final onDbVaFinish onDbVaFinish ){

        database.getReference("Albums").child(recomendationModel.getUserModel().getId()).child(recomendationModel.getAlbumModel().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class) ;
                recomendationModel.setAlbumModel(albumModel);
                onDbVaFinish.onfinish(recomendationModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    void get_rec_item( final  RecomendationModel recomendationModel  , final onDbVaFinish onDbVaFinish ){

        database.getReference("AlbumsItems").child(recomendationModel.getAlbumModel().getId()).child(recomendationModel.getItemModel().getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final AlbumModel albumModel = dataSnapshot.getValue(AlbumModel.class) ;
                recomendationModel.setItemModel(albumModel);
                onDbVaFinish.onfinish(recomendationModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    void get_rec_dataa(){

        Query usersdb1 = database.getReference("Recommendations").limitToLast(3);
        usersdb1.keepSynced(true);

        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren())
                {

                    String item = child.getKey() ;
                    final  String album = child.getChildren().iterator().next().getKey() ;
                    final  String user = child.getChildren().iterator().next().getChildren().iterator().next().getKey() ;
                    Log.e("rec_data"  , " user "+ user + " album " +  album +"  "+ item) ;
                    UserModel userModelModel = new UserModel();
                    userModelModel.setId(user);
                    AlbumModel albumModel = new AlbumModel() ;
                    albumModel.setId(album);
                    AlbumModel itemModel = new AlbumModel() ;
                    itemModel.setId(item);
                    RecomendationModel recomendationModel = new RecomendationModel() ;
                    recomendationModel.setItemModel(itemModel);
                    recomendationModel.setAlbumModel(albumModel);
                    recomendationModel.setUserModel(userModelModel);
                    get_rec_data(recomendationModel);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private int getIndexForRecKey(String key) {
        int index = 0;
        for (RecomendationModel recomendationModel : recommendedArrayList) {
            Log.e("rec_inx" , recomendationModel.getItemModel().getId()  +"   "  + key) ;

            if (recomendationModel.getItemModel().getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  getLikes(final RecomendationModel  recomendationModel , final onDbVaFinish onDbVaFinish){
        Query usersdb = database.getReference("ItemsLikes/"+recomendationModel.getItemModel().getId()+"/"+ AppContoller.getInstance().getPrefManager().getUser().getId());
        usersdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){
                    recomendationModel.getItemModel().setLiked(false);
                }else {
                    recomendationModel.getItemModel().setLiked(true);

                }

                onDbVaFinish.onfinish(recomendationModel);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


    void  get_events( ) {


         database.getReference("Events").limitToLast(1)
                 .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {

                    Log.e("home" , " Events" + dataSnapshot.toString()) ;
                    if (!dataSnapshot.getChildren().iterator().hasNext()){

                        return;
                    }

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        final EventsModel newsModel = child.getValue(EventsModel.class);
                          Glide.with(getActivity()).load(newsModel.getImgs().get(0))
                                .transition(new DrawableTransitionOptions().crossFade())
                                .apply(new RequestOptions().centerCrop())
                                .apply(new RequestOptions().circleCrop())
                                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(events_icon);
                    }

                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }) ;


    }



    void  get_news( ) {


        database.getReference("News").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {

                            Log.e("home" , " Events" + dataSnapshot.toString()) ;
                            if (!dataSnapshot.getChildren().iterator().hasNext()){

                                return;
                            }

                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                final NewsModel newsModel = child.getValue(NewsModel.class);
                                Glide.with(getActivity()).load(newsModel.getImg())
                                        .transition(new DrawableTransitionOptions().crossFade())
                                        .apply(new RequestOptions().centerCrop())
                                        .apply(new RequestOptions().circleCrop())
                                        .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                                        .into(news_img);
                            }

                        }catch (Exception e){}
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }) ;


    }



    @OnClick(R.id.news_img)
    void open_news(){
        NewsListFragment newsListFragment  = new NewsListFragment() ;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.home_content_frame, newsListFragment);
        fragmentTransaction.addToBackStack("main_news");;
        fragmentTransaction.commit();
    }

    @OnClick(R.id.events_icon)
    void open_events(){
        EventsListFragment eventsListFragment  = new EventsListFragment() ;
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.home_content_frame, eventsListFragment);
        fragmentTransaction.addToBackStack("main_events");;
        fragmentTransaction.commit();
    }


    @OnClick(R.id.blogs_img)
    void open_blogs(){
        CatModel catModel =  new CatModel(getString(R.string.Blogs) , R.drawable.cosmetics) ;
        BlogsDesignersListFragment searchDesignersListFragment = new BlogsDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.home_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("main_blogs");
        fragmentTransaction.commit();
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
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            fragmentTransaction.replace(R.id.home_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack(from_tab+"_album_details");
        fragmentTransaction.commit();


    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }




    private interface  onDbVaFinish{
        void onfinish  (Object object);
    }
}
