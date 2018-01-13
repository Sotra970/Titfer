package com.titfer.Fragments.Events;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.titfer.Models.EventsModel;
import com.titfer.Models.NewsModel;
import com.titfer.R;
import com.titfer.Widget.WrapContentViewPager;
import com.titfer.adapter.EventsImagePagerAdapter;
import com.titfer.app.AppContoller;
import com.titfer.app.Constants;
import com.titfer.intefraces.PosHolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsDetailsFragment extends Fragment implements PosHolderListener {




    public EventsDetailsFragment() {
        // Required empty public constructor
    }


    EventsModel eventsModel ;

    public void setEventsModel(EventsModel eventsModel) {
        this.eventsModel = eventsModel;
    }

    FirebaseDatabase database  ;
    View res_layout  ;




    String from_tab ;

    public void setFrom_tab(String from_tab) {
        this.from_tab = from_tab;
    }

    @BindView(R.id.bar_title)
    TextView bar_title ;


    @BindView(R.id.caption)
    TextView caption ;
   @BindView(R.id.edcaption)
    EditText ed_caption ;


    @BindView(R.id.desc)
    TextView desc ;
    @BindView(R.id.ed_desc)
    EditText ed_desc ;

    @BindView(R.id.date)
    TextView date ;

    @BindView(R.id.ed_date)
    EditText ed_date ;

    @BindView(R.id.edit_caption_done)
    ImageView edit_caption_done ;


    @BindView(R.id.edit_desc_done)
    ImageView edit_desc_done ;


    @BindView(R.id.edit_date_done)
    ImageView edit_date_done ;


    @BindView(R.id.imgs_pager)
    WrapContentViewPager imgs_pager ;
    ArrayList<String> imgs  = new ArrayList<>() ;
    EventsImagePagerAdapter eventsImagePagerAdapter ;



    @BindView(R.id.edit_caption_action)
    View edit_caption_action;

    @BindView(R.id.edit_desc_action)
    View edit_desc_action ;

    @BindView(R.id.edit_date_action)
    View edit_date_action ;


    @BindView(R.id.delete_photo)
    View delete_photo ;

    @BindView(R.id.settings)
    View settings ;


    @OnClick(R.id.bar_back)
    void bar_back()
    {

        getActivity().onBackPressed();
    }


    @OnClick(R.id.edit_caption_action)
    void edit_caption(){
        edit_caption_done.setVisibility(View.VISIBLE);
        ed_caption.setVisibility(View.VISIBLE);
        ed_caption.requestFocus();
        caption.setVisibility(View.GONE);
        showsettings();
    }


    @OnClick(R.id.edit_desc_action)
    void edit_desc(){
        edit_desc_done.setVisibility(View.VISIBLE);
        ed_desc.setVisibility(View.VISIBLE);
        ed_desc.requestFocus();
        desc.setVisibility(View.GONE);
        showsettings();
    }


    @OnClick(R.id.edit_date_action)
    void edit_date(){
        edit_date_done.setVisibility(View.VISIBLE);
        ed_date.setVisibility(View.VISIBLE);
        ed_date.requestFocus();
        date.setVisibility(View.GONE);
        showsettings();
    }



    @OnClick(R.id.delete_photo)
    void delete_photo(){
        showsettings();
        showloading(true) ;
        database.getReference("Events/"+eventsModel.getId())
                .setValue(null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext() , "item not deleted pleas try again " , Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                new AlertDialog.Builder(getActivity()).setMessage("item has been deleted ")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().onBackPressed();
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            }
        });
    }


    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


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

    @OnClick(R.id.edit_caption_done)
    void edit_caption_done(){
        if (TextUtils.isEmpty(ed_caption.getText().toString())){
            ed_caption.setError(getString(R.string.empty_field_caption));
            return;
        }
        ed_caption.setError(null);
        edit_caption_done.setVisibility(View.GONE);
        ed_caption.setVisibility(View.GONE);
        caption.setText(ed_caption.getText().toString());
        caption.setVisibility(View.VISIBLE);
        bar_title.setText(caption.getText().toString());
        database.getReference("Events/"+eventsModel.getId()+"/title")
                .setValue(caption.getText().toString()); ;
    }



    @OnClick(R.id.edit_desc_done)
    void edit_desc_done(){
        if (TextUtils.isEmpty(ed_desc.getText().toString())){
            ed_desc.setError(getString(R.string.empty_field_caption));
            return;
        }
        ed_desc.setError(null);
        edit_desc_done.setVisibility(View.GONE);
        ed_desc.setVisibility(View.GONE);
        desc.setText(ed_desc.getText().toString());
        desc.setVisibility(View.VISIBLE);
        database.getReference("Events/"+eventsModel.getId()+"/desc")
                .setValue(desc.getText().toString()); ;
    }


    @OnClick(R.id.edit_date_done)
    void edit_date_done(){
        if (TextUtils.isEmpty(ed_date.getText().toString())){
            ed_date.setError(getString(R.string.empty_field));
            return;
        }
        ed_date.setError(null);
        edit_date_done.setVisibility(View.GONE);
        ed_date.setVisibility(View.GONE);
        date.setText(ed_date.getText().toString());
        date.setVisibility(View.VISIBLE);
        database.getReference("Events/"+eventsModel.getId()+"/date")
                .setValue(date.getText().toString()); ;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (res_layout == null){




                    database = FirebaseDatabase.getInstance(Constants.Ref);
            res_layout = inflater.inflate(R.layout.fragment_eventss_details, null, false);
            ButterKnife.bind(this , res_layout) ;

            if (savedInstanceState !=null){
                eventsModel = (EventsModel) savedInstanceState.getSerializable("eventsModel");

            }


            if (AppContoller.getInstance().getPrefManager().getUser().getType() ==  2 ){
                settings.setVisibility(View.VISIBLE);
            }else settings.setVisibility(View.GONE);

            bar_title.setText(eventsModel.getTitle());

            caption.setText(eventsModel.getTitle());
            ed_caption.setText(eventsModel.getTitle());

            desc.setText(eventsModel.getDesc());
            ed_desc.setText(eventsModel.getDesc());


            date.setText(eventsModel.getDate());
            ed_date.setText(eventsModel.getDate());



            imgs = eventsModel.getImgs();
            Log.e("details image count " , imgs.size() +"");
            eventsImagePagerAdapter = new EventsImagePagerAdapter(getContext(),imgs , this) ;
            eventsImagePagerAdapter.notifyDataSetChanged();
            imgs_pager.setAdapter(eventsImagePagerAdapter);




        }
        return  res_layout ;
    }






    boolean settingsOpend = false ;
    @OnClick(R.id.settings)
    void showsettings(){
        if (!settingsOpend){
            settings.setClickable(false);
            edit_caption_action.setVisibility(View.VISIBLE);
            delete_photo.setVisibility(View.VISIBLE);
            edit_desc_action.setVisibility(View.VISIBLE);
            edit_date_action.setVisibility(View.VISIBLE);


            edit_caption_action.animate().alpha(1).setDuration(300).setStartDelay(0).setInterpolator(new FastOutLinearInInterpolator());
            edit_desc_action.animate().alpha(1).setDuration(300).setStartDelay(100).setInterpolator(new FastOutLinearInInterpolator());
            edit_date_action.animate().alpha(1).setDuration(300).setStartDelay(100).setInterpolator(new FastOutLinearInInterpolator());
            delete_photo.animate().alpha(1).setStartDelay(200).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());

            settingsOpend = true ;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    settings.setClickable(true);

                }
            } , 500);

        }else {
            settings.setClickable(false);
            edit_caption_action.animate().alpha(0).setStartDelay(200).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());
            edit_desc_action.animate().alpha(0).setStartDelay(100).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());
            edit_date_action.animate().alpha(0).setStartDelay(100).setDuration(300).setInterpolator(new FastOutLinearInInterpolator());
            delete_photo.animate().alpha(0).setDuration(300).setStartDelay(0).setInterpolator(new FastOutLinearInInterpolator());

            settingsOpend = false ;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    edit_caption_action.setVisibility(View.GONE);
                    edit_desc_action.setVisibility(View.GONE);
                    edit_date_action.setVisibility(View.GONE);
                    delete_photo.setVisibility(View.GONE);
                    settings.setClickable(true);

                }
            } , 500);
        }

    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("eventsModel", eventsModel);
    }


    @Override
    public void onHolderClicked(View view, Object child, int pos) {

    }
}
