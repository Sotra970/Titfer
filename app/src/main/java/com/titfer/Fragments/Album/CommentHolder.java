package com.titfer.Fragments.Album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.R;

/**
 * Created by sotra on 9/10/2017.
 */



public class CommentHolder extends RecyclerView.ViewHolder {
    TextView comment_name ;

    TextView comment_text ;

    ImageView comment_img ;


    public CommentHolder(View itemView) {
        super(itemView);
        comment_name = (TextView) itemView.findViewById(R.id.comment_name);
        comment_text = (TextView) itemView.findViewById(R.id.comment_text);
        comment_img = (ImageView) itemView.findViewById(R.id.comment_img);

    }

    public void setName(String name) {
        comment_name.setText(name);
    }

    public void setText(String name) {
        comment_text.setText(name);
    }


    public void setImg(String img) {
        Glide.with(comment_img.getContext())
                .load(img)
                .apply(new RequestOptions().centerCrop())
                .apply(new RequestOptions().circleCrop())
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .transition(new DrawableTransitionOptions().crossFade())
                .thumbnail(0.5f)
                .into(comment_img);
    }


}
