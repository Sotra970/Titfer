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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.Models.NotificationModel;
import com.titfer.Models.NotificationModel;
import com.titfer.R;
import com.titfer.intefraces.HolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<NotificationModel> data ;
    AppCompatActivity context ;
    LayoutInflater inflater ;
    HolderListener holderListener ;

    private boolean showLoader = true;


    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public NotificationsAdapter(ArrayList<NotificationModel> data, AppCompatActivity context , HolderListener holderListener) {
        this.data = data;
        this.context = context;
        this.holderListener = holderListener;
        inflater = LayoutInflater.from(context) ;
    }


    public NotificationModel getItem(int postion){
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
        View v1 = inflater.inflate(R.layout.noti_item, parent, false);
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
            final NotificationModel current = data.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.onHolderClicked(v , current);
                }
            });


        holder1.date.setText(current.getDate());
        holder1.message.setText(current.getMessage());
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

        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.message)
        TextView message;
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
