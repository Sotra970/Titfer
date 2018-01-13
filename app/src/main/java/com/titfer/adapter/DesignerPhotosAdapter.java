package com.titfer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.Models.AlbumModel;
import com.titfer.R;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sotra on 9/25/2016.
 */
public class DesignerPhotosAdapter extends  RecyclerView.Adapter<DesignerPhotosAdapter.ViewHolder > {
    private Activity context ;
    private ArrayList<AlbumModel> data ;
    private LayoutInflater layoutInflater ;
    public DesignerPhotosAdapter(Activity context, ArrayList<AlbumModel> data)  {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        Log.e("newAdapter","size" + data.size());
    }

    public AlbumModel getItem(int postion){
        return  data.get(postion);
    }


    @Override
    public DesignerPhotosAdapter.ViewHolder  onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.photo_list_item,parent,false);
       final  ViewHolder  ViewHolder  = new ViewHolder (view);
        return ViewHolder ;
    }



    @Override
    public void onBindViewHolder(DesignerPhotosAdapter.ViewHolder  holder, final int position) {
        final AlbumModel currentElement =  data.get(position);
        Glide.with(context).load(currentElement.getImg())
                .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().circleCrop())

                .into(holder.img);
             Log.e("course member adapter","onb"  );
    }

 

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void insertData(AlbumModel data){
        this.data.add(data);
        notifyDataSetChanged();
    }
    public void refresh(ArrayList<AlbumModel> data) {
        this.data = data ;
        notifyDataSetChanged();
    }

    public class  ViewHolder  extends RecyclerView.ViewHolder {
        @BindView(R.id.img)
        ImageView img;
        public ViewHolder (View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }
    
}
