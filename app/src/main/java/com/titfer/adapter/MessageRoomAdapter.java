package com.titfer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.titfer.R;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;

import java.util.List;


/**
 * Created by sotra on 9/25/2016.
 */
public class MessageRoomAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context ;
    private List<Room_tabel> data ;
    private LayoutInflater layoutInflater ;
    int bg_color ;
    private String uploaded_img_name;

    public MessageRoomAdapter(Activity context, List<Room_tabel> data  )  {
        this.context = context;
        this.data = data;
        this.bg_color = bg_color ;
        layoutInflater = LayoutInflater.from(context);
        Log.e("newAdapter","size" + data.size());

    }
    public Room_tabel getItem(int postion){
        return  data.get(postion);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.room_item,parent,false);
            TextViewHolder coursesViewHolder = new TextViewHolder(view);
            return coursesViewHolder;
      
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Room_tabel currentElement =  data.get(position);
        Mess_tabel last_mess =  new Mess_tabel().last_mess(currentElement.room_id);

                ((TextViewHolder) holder).name.setText(currentElement.name);
                ((TextViewHolder) holder).timestamp.setText(last_mess.timeStamp);


            ((TextViewHolder) holder).message.setText(last_mess.message);

        int unseen = new Room_tabel().unseenCount(currentElement.room_id) ;
        if (unseen > 0){
            ((TextViewHolder) holder).chat_notifty.setText(unseen+"");
            ((TextViewHolder) holder).chat_notifty.setVisibility(View.VISIBLE);
        }

        Glide.with(context)
                .asDrawable()
                .load(currentElement.img)
                .transition( new DrawableTransitionOptions().crossFade())
                .thumbnail(0.5f)
                .apply(new RequestOptions().fitCenter())
                .apply(new RequestOptions().centerCrop())
                .into(((TextViewHolder) holder).img) ;


        Log.e("message_adapter","img " + currentElement.img  );
        Log.e("message_adapter","name "+ currentElement.name  );
    }

  

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void insertData(Room_tabel data){
        this.data.add(data);
        notifyDataSetChanged();
    }
    public void refresh(List<Room_tabel> data) {
        this.data = data ;
        notifyDataSetChanged();
    }

    public class  TextViewHolder extends RecyclerView.ViewHolder {
        TextView name , message , timestamp  , chat_notifty;
        ImageView img ;
        public TextViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.it_text);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            chat_notifty = (TextView) itemView.findViewById(R.id.chat_notify);
            img = (ImageView) itemView.findViewById(R.id.it_img);
        }
    }

    




}
