package com.titfer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.ReportModel;
import com.titfer.R;
import com.titfer.intefraces.ReportHolderListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProplemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<ReportModel> data ;
    AppCompatActivity context ;
    LayoutInflater inflater ;
    ReportHolderListener holderListener ;

    private boolean showLoader = true;


    private static final int ITEM = 0;
    private static final int LOADING = 1;

    public ProplemsAdapter(ArrayList<ReportModel> data, AppCompatActivity context , ReportHolderListener holderListener) {
        this.data = data;
        this.context = context;
        this.holderListener = holderListener;
        inflater = LayoutInflater.from(context) ;
    }


    public ReportModel getItem(int postion){
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
        View v1 = inflater.inflate(R.layout.news_categoriews_item, parent, false);
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
            final ReportModel current = data.get(position);


            Glide.with(context).load(current.getUserModel().getProfilePic())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().circleCrop())
                    .into(holder1.img);
        holder1.name.setText(current.getUserModel().getFirstName() + " " +current.getUserModel().getLastName());
        holder1.bio.setText(current.getProblem());

            ((ViewHolder) holder).bio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.onHolderProplemClicked(current.getProblem());
                }
            });
            ((ViewHolder) holder).img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holderListener.onHolderClicked(v , current.getUserModel());
                }
            });
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


    void recycleSetUP(ArrayList<AlbumModel> current , RecyclerView list ) {
        DesignerPhotosAdapter adapter = new DesignerPhotosAdapter(context, current);
        RecyclerView.LayoutManager layoutManager ;
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL , false) ;
        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView ;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.bio)
        TextView bio;
        @BindView(R.id.name)
        TextView name;
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
