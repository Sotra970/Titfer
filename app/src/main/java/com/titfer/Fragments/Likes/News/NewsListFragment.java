package com.titfer.Fragments.News;

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
import com.titfer.Models.NewsModel;
import com.titfer.R;
import com.titfer.Utils.EndlessRecyclerViewScrollListener;
import com.titfer.adapter.NewsAdapter;
import com.titfer.app.Constants;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewsListFragment extends Fragment implements HolderListener {

    FirebaseDatabase database  ;


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.bar_title)
    TextView bar_title ;


    @BindView(R.id.no_search_results)
    TextView no_news;

    View res_layout ;
    ArrayList<NewsModel> newsModels = new ArrayList<>();
    private NewsAdapter adapter;
    public NewsListFragment() {
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
                mPageEndOffset = savedInstanceState.getInt("mPageEndOffset");
                newsModels = (ArrayList<NewsModel>) savedInstanceState.getSerializable("newsModels");
            }else {
              get_data();
            }
            bar_title.setText("news");
            news_feed_setup();
            adapter.notifyDataSetChanged();
        }
        return res_layout;

    }





    void news_feed_setup( ) {

        adapter = new NewsAdapter(newsModels,(AppCompatActivity) getActivity() , this);
        LinearLayoutManager layoutManager ;
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                get_data(newsModels.get(newsModels.size()-1).getId());
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
        for (NewsModel newsModel : newsModels) {
            if (newsModel.getId().equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    void  get_data( ) {
        showLoading(true);


        usersdb1 = database.getReference("News").limitToFirst(mPageLimit);
        usersdb1.keepSynced(true);





        usersdb1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    try {

                        Log.e("home" , " news" + dataSnapshot.toString()) ;
                        if (!dataSnapshot.getChildren().iterator().hasNext()){
                            showLoading(false);
                            adapter.notifyDataSetChanged();
                            if (newsModels.size() == 0 ){
                                no_news.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        for (DataSnapshot child : dataSnapshot.getChildren()){
                            final NewsModel newsModel = child.getValue(NewsModel.class);

                            int pos = getIndexForKey(newsModel.getId());
                            if (pos == -1) {
                                showLoading(false);
                                adapter.showLoading(false);
                                newsModels.add(newsModel);
                                adapter.notifyDataSetChanged();
                            } else {
                                newsModels.set(pos,newsModel);
                                adapter.notifyItemChanged(pos);
                            }
                        }

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



        usersdb2 = database.getReference("News").startAt(lastSeenID).limitToFirst(mPageLimit);
        usersdb2.keepSynced(true);

        adapter.showLoading(true);

        usersdb2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("News page" , dataSnapshot.toString()) ;
                if (!dataSnapshot.getChildren().iterator().hasNext()){
                    adapter.showLoading(false);
                    adapter.notifyDataSetChanged();
                    return;
                }

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                                final NewsModel newsModel = child.getValue(NewsModel.class);

                                        int pos = getIndexForKey(newsModel.getId());
                                        if (pos == -1) {
                                            showLoading(false);
                                            adapter.showLoading(false);
                                            newsModels.add(newsModel);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            newsModels.set(pos,newsModel);
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
    public void onHolderClicked(View view, Object child) {
        NewsDetailsFragment newsDetailsFragment = new NewsDetailsFragment();
        newsDetailsFragment.setNewsModel((NewsModel) child);
        newsDetailsFragment.setFrom_tab("main_news");
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager() .beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.home_content_frame, newsDetailsFragment);
        fragmentTransaction.addToBackStack("main_news");
        fragmentTransaction.commit();
    }

    @Override
    public void onHolderLongClicked(View view, Object child) {

    }

}
