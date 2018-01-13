package com.titfer.Fragments.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.titfer.Models.CatModel;
import com.titfer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFashionBrandsFragment extends Fragment  {


    CatModel catModel ;

    public void setCatModel(CatModel catModel) {
        this.catModel = catModel;
    }

    public SearchFashionBrandsFragment() {
        // Required empty public constructor
    }



    View res_layout ;
    @BindView(R.id.bar_title)
    TextView bar_title ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null ){
            res_layout = inflater.inflate(R.layout.fragment_fashion_brands_search_sub_cat, container, false);
            ButterKnife.bind(this , res_layout);
            if (savedInstanceState !=null){
                catModel = (CatModel) savedInstanceState.getSerializable("cat");
            }
            bar_title.setText(catModel.getName());
        }
        return  res_layout ;
    }
    @OnClick(R.id.bar_back)
    void bar_back(){
        getActivity().onBackPressed();
    }



    @OnClick(R.id.couture)
    public void couture() {
        CatModel catModel =  new CatModel(getString(R.string.Couture) , R.drawable.couture) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }


    @OnClick(R.id.ready)
    public void ready() {
        CatModel catModel =  new CatModel(getString(R.string.Ready2wear) , R.drawable.ready) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }



    @OnClick(R.id.sportswear)
    public void sportswear() {
        CatModel catModel =  new CatModel(getString(R.string.Sportswear) , R.drawable.sportswear) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }



    @OnClick(R.id.bags)
    public void bags() {
        CatModel catModel =  new CatModel(getString(R.string.Shoesandbags) , R.drawable.bags) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }



    @OnClick(R.id.weeding)
    public void weeding() {
        CatModel catModel =  new CatModel(getString(R.string.Wedding) , R.drawable.weeding) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }



    @OnClick(R.id.abaya)
    public void abaya() {
        CatModel catModel =  new CatModel(getString(R.string.Abaya) , R.drawable.abaya) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }






    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("cat", catModel );
    }
}
