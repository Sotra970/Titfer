package com.titfer.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.AlbumModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.intefraces.Add_album_listener;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {


    private ArrayList<AlbumModel> data;
    private Context context;
    HolderListener holderListener;
    Add_album_listener add_album_listener;

    boolean add_item ;

    public AlbumAdapter(ArrayList<AlbumModel> data, Context context, HolderListener holderListener  , Add_album_listener add_album_listener, boolean add_itm ) {
        this.data = data;
        this.context = context;
        this.holderListener = holderListener;
        if (AppContoller.getInstance().getPrefManager().getUser().getType()==1)
        add_item = add_itm ;
        else add_item = false ;
        this.add_album_listener = add_album_listener ; 
    }




    public ArrayList<AlbumModel> getData() {
        return data;
    }

    public void setData(ArrayList<AlbumModel> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v1 = inflater.inflate(R.layout.album_item, parent, false);
        return new ViewHolder(v1);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if (add_item && position==0 ){
            holder.add_plus.setVisibility(View.VISIBLE);
            
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    add_album_listener.onHolderClicked(view);
                }
            });
//            holder.title_bg.post(new Runnable() {
//                @Override
//                public void run() {
//                    ViewGroup.LayoutParams params = holder.title_bg.getLayoutParams() ;
//                    params.width = (int) (holder.itemView.getMeasuredWidth()*3)/4;
//                    holder.title_bg.setLayoutParams(params);
//
//                }
//            });

        }else {
            position = add_item ? position -1 :position ;
            final AlbumModel current = data.get(position);
            holder.name.setText(current.getTitle());
//
            Glide.with(context)
                    .load(current.getImg())
                    .transition( new DrawableTransitionOptions().crossFade())
                    .thumbnail(0.5f)
                    .apply(new RequestOptions().centerCrop())
                    .into(holder.img) ;
            Log.e("profile_ada_img_url" , current.getImg()) ;



            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holderListener.onHolderClicked(holder.img,current);
                }
            });


            holder.img.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (add_item == true)
                        holderListener.onHolderLongClicked(holder.img,current);
                    return false;
                }
            });



        }



    }

    @Override
    public int getItemCount() {
        if (add_item){
            return  data.size()+1 ;
        }

        return data.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
         ImageView img;
        @BindView(R.id.title)
         TextView name;

        @BindView(R.id.title_bg)
        View title_bg;


        @BindView(R.id.add_plus_icon)
        View add_plus;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);

        }

    }




}
