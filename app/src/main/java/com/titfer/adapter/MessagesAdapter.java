package com.titfer.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.titfer.R;
import com.titfer.intefraces.MessageClickListener;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by sotra on 9/25/2016.
 */
public class MessagesAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity context ;
    private List<Mess_tabel> data ;
    private LayoutInflater layoutInflater ;
    public MessagesAdapter(Activity context, List<Mess_tabel> data  )  {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        Log.e("newAdapter","size" + data.size());

    }
    public Mess_tabel getItem(int postion){
        return  data.get(postion);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


                View view = layoutInflater.inflate(R.layout.message_item,parent,false);
                TextViewHolder coursesViewHolder = new TextViewHolder(view);
                return coursesViewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.e("message adapter " , "postion  " +position);
        final Mess_tabel currentElement =data.get(position);


            Log.e("message dir ","   "+currentElement.dir  );


        Log.e("course member adapter","onb"  );


                if (currentElement.dir == 1){
                    ((TextViewHolder) holder).message_container.setGravity(Gravity.LEFT);
                }else{
                    ((TextViewHolder) holder).message_container.setGravity(Gravity.RIGHT);
                }
                ((TextViewHolder) holder).from.setText(currentElement.user_name);
                ((TextViewHolder) holder).timestamp.setText(currentElement.timeStamp);
                ((TextViewHolder) holder).message.setText(currentElement.message);






    }

  

    @Override
    public int getItemCount() {
        return data.size();
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (Mess_tabel mess_tabel : data) {
            if (mess_tabel.mess_id == null){
                index++ ;
            }
            else if (mess_tabel.mess_id.equalsIgnoreCase(key)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }


    public void insertData(Mess_tabel data){
        Log.e("mess_adapter" , "insert_data" +data.message  +"   "+data.mess_id) ;

        try {
            if (getIndexForKey(data.mess_id) == -1){
                this.data.add(0,data);
                notifyDataSetChanged();

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void refresh(List<Mess_tabel> data) {
        this.data = data ;
        notifyDataSetChanged();
    }

    public class  TextViewHolder extends RecyclerView.ViewHolder {
        TextView  message , timestamp , from;
        LinearLayout message_container  ;
        public TextViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            from = (TextView) itemView.findViewById(R.id.mess_from);
            message_container =(LinearLayout)itemView.findViewById(R.id.message_container);
        }
    }








}
