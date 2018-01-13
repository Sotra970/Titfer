package com.titfer.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.R;
import com.titfer.intefraces.HolderListener;
import com.titfer.intefraces.PosHolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sotra on 9/7/2016.
 */
public class EventsImagePagerAdapter extends PagerAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater;
    private ArrayList<String> mResources;

    PosHolderListener holderListener ;

    public EventsImagePagerAdapter(Context mContext , ArrayList<String> data  , PosHolderListener posHolderListener   ) {
        this.mContext = mContext;
       mLayoutInflater = LayoutInflater.from(mContext);
        mResources = data ;
        this.holderListener = posHolderListener ;
    }



    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return mResources.size();
    }
    public void addItem(String ResList){
        mResources.add(ResList);
        notifyDataSetChanged();
    }


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @BindView(R.id.event_img)
    ImageView event_img ;

    @Override
    public Object instantiateItem(ViewGroup container, final int position)  {
        final View itemView = mLayoutInflater.inflate(R.layout.event_img_item, container, false);
        ButterKnife.bind(this , itemView) ;

        Log.e("events adapter " , mResources.get(position)) ;

        Glide.with(mContext)
                .load(mResources.get(position))
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .transition(new DrawableTransitionOptions().crossFade())
                .thumbnail(0.5f)
                .into(event_img);

        event_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderListener.onHolderClicked( event_img , mResources.get(position)  , position);
            }
        });
        container.addView(itemView);
        return itemView;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((FrameLayout) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
          return view == object;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
