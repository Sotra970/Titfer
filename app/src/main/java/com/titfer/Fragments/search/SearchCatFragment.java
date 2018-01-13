package com.titfer.Fragments.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.titfer.Models.CatModel;
import com.titfer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchCatFragment extends Fragment {

  ;


    public SearchCatFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.key_word)
    EditText key_word ;
    View res_layout ;
    FirebaseDatabase database ;
    Query albums_db ;

    @OnClick(R.id.toolbar_search)
    void searc_click(){
        search();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null ){
            res_layout = inflater.inflate(R.layout.fragment_search, container, false);
            ButterKnife.bind(this , res_layout);
            key_word.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            key_word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    search();
                    return true;
                }
            });
        }
        return  res_layout ;
    }


    private void search() {
        String key_word_text = key_word.getText().toString().trim() ;
        if (TextUtils.isEmpty(key_word_text)){
        return;
        }

        key_word.setText("");
        CatModel catModel = new CatModel(key_word_text ,  R.drawable.menswear) ;
        SearchDesignersKeyWordListFragment searchSubCatFragment = new SearchDesignersKeyWordListFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();

    }


    @OnClick(R.id.fashion_brands)
    void fashion_brands(){
        CatModel catModel = new CatModel(getString(R.string.Fashion_Designers) , R.drawable.fashion2) ;
        SearchFashionBrandsFragment searchSubCatFragment = new SearchFashionBrandsFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();
    }


    @OnClick(R.id.fashion_designers)
    void fashion_designers(){
        CatModel catModel = new CatModel(getString(R.string.Fashion_Designers) , R.drawable.fashion2) ;
        SearchFashionDesignersFragment searchSubCatFragment = new SearchFashionDesignersFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();
    }


    @OnClick(R.id.jewelry)
    void jewelry(){
        CatModel catModel = new CatModel(getString(R.string.MensWear) , R.drawable.menswear) ;
        SearchAccessoriesFragment searchSubCatFragment = new SearchAccessoriesFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();
    }


    @OnClick(R.id.hairdressers)
    void hairdressers(){
        CatModel catModel =  new CatModel(getString(R.string.Hairdressers) , R.drawable.hairdressers) ;
        SearchDesignersListFragment searchDesignersListFragment = new SearchDesignersListFragment() ;
        searchDesignersListFragment.setCatModel(catModel);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchDesignersListFragment);
        fragmentTransaction.addToBackStack("search_cats");
        fragmentTransaction.commit();
    }

    @OnClick(R.id.makeup)
    void makeup(){
        CatModel catModel = new CatModel(getString(R.string.MensWear) , R.drawable.menswear) ;
        SearchMakeUpFragment searchSubCatFragment = new SearchMakeUpFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();
    }

    @OnClick(R.id.MensWear)
    void professional(){
        CatModel catModel = new CatModel(getString(R.string.MensWear) , R.drawable.menswear) ;
        SearchMensWearFragment searchSubCatFragment = new SearchMensWearFragment() ;
        searchSubCatFragment.setCatModel(catModel);
        FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        fragmentTransaction.replace(R.id.serch_content_frame, searchSubCatFragment);
        fragmentTransaction.addToBackStack("search_cats");;
        fragmentTransaction.commit();
    }







}
