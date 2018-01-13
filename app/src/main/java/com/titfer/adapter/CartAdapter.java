package com.titfer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.titfer.Models.CartModel;
import com.titfer.Models.EventsModel;
import com.titfer.R;
import com.titfer.intefraces.CartListener;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CartModel> data ;
    AppCompatActivity context ;
    LayoutInflater inflater ;
    CartListener holderListener ;

    private boolean showLoader = true;


    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public CartAdapter(ArrayList<CartModel> data, AppCompatActivity context , CartListener holderListener) {
        this.data = data;
        this.context = context;
        this.holderListener = holderListener;
        inflater = LayoutInflater.from(context) ;
    }


    public CartModel getItem(int postion){
        return  data.get(postion);
    }

    public void showLoading(boolean status) {
        showLoader = status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.cart_item, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }


    @Override
    public int getItemViewType(int position) {
        return position != 0 && position == getItemCount() - 1  ? LOADING : ITEM ;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == LOADING){
            final LoadingViewHolder loaderViewHolder = (LoadingViewHolder) holder;
            if (showLoader) {
                loaderViewHolder.mProgressBar.setVisibility(View.VISIBLE);
            } else {
                loaderViewHolder.mProgressBar.setVisibility(View.GONE);
            }
        }else {

            ViewHolder holder1 = (ViewHolder) holder ;
            final CartModel current = data.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.onHolderClicked(v , current);
                }
            });

            if (current.getState().equals("confirmed") || current.getState().equals("cancel")){
                ((ViewHolder) holder).action_con.setVisibility(View.GONE);
            }else {
                ((ViewHolder) holder).action_con.setVisibility(View.VISIBLE);
                ((ViewHolder) holder).cart_confirm_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holderListener.onConfirm(v , current);
                    }
                });

                ((ViewHolder) holder).cart_cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holderListener.onCancel(v , current);
                    }
                });

            }
            ((ViewHolder) holder).cart_designer_name.setText(current.getDesigner_name());
            ((ViewHolder) holder).cart_item_price.setText(current.getPrice());
            ((ViewHolder) holder).cart_shipping_time.setText(current.getDate());



            Glide.with(context).load(current.getImg())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder1.cart_img);

        }

    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (data == null || data.size() == 0) {
            return 0;
        }

        // +1 for loader
        return data.size() + 1;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cart_img)
        ImageView cart_img;
        @BindView(R.id.cart_designer_name)
        TextView cart_designer_name;
        @BindView(R.id.cart_item_price)
        TextView cart_item_price;
        @BindView(R.id.cart_shipping_time)
        TextView cart_shipping_time;


        @BindView(R.id.cart_confirm_btn)
        View cart_confirm_btn;
         @BindView(R.id.cart_cancel_btn)
         View cart_cancel_btn;
        @BindView(R.id.action_con)
         View action_con;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this , itemView);
        }
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.loadmore_progress)
        View mProgressBar ;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


}
