package com.titfer.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.Models.RecomendationModel;
import com.titfer.R;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.RecommendedHolder>{

    Context context;
    ArrayList<RecomendationModel>recommendedImg;
    HolderListener holderListener ;

    boolean item_name_visible ;
    public RecommendedAdapter(Context context, ArrayList<RecomendationModel> recommendedImg , HolderListener holderListener) {
        this.context = context;
        this.recommendedImg = recommendedImg;
        this.holderListener = holderListener ;
        this.item_name_visible = false;
    }





    @Override
    public RecommendedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.circles_item, parent, false);
        RecommendedHolder RecommendedHolder = new RecommendedHolder(item);
        return RecommendedHolder;
    }

    @Override
    public void onBindViewHolder(final RecommendedHolder holder, final int position) {


        Glide.with(context).load(recommendedImg.get(position).getItemModel().getImg())
                .transition(new DrawableTransitionOptions().crossFade())
                .apply(new RequestOptions().centerCrop())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.recommendedImage);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderListener.onHolderClicked(v ,recommendedImg.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return recommendedImg.size() <= 3 ? recommendedImg.size() : 3  ;

    }

    public class RecommendedHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.recommended_img)
        ImageView recommendedImage;

        @BindView(R.id.recommended_txt)
        TextView recommended_txt;
        public RecommendedHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

}

}
