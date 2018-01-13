package com.titfer.holder;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.titfer.R;

import butterknife.BindView;


/**
 * Created by Sotraa on 6/13/2016.
 */
public class TabIconHolder {
    private final View view;
    ImageView tabImage ;
    TextView  notfication;
    LayoutInflater inflater ;
    String tab ;
    public TabIconHolder(int icon  , String tab_key , Activity context){
        inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.tab_icon, null);

//        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tabImage = (ImageView) view.findViewById(R.id.holderTabIcon);
        tabImage.setImageResource(icon);
        notfication = (TextView) view.findViewById(R.id.holderTabNotification);
       tab= tab_key;
     //   RefrechNotfication();
    }
    public View getView (){
        return view;
    }
  /*  public void RefrechNotfication(){
        switch (tab){
            case 0 : {
                notficationCount = ConnStatesCallBack.getInstance().getPrefManager().getNotifications(Config.KEY_NEWS_FEED_NOTIFICATIONS);

            }

        }
        if ( notficationCount !=0){

            notfication.setText(notficationCount+"");
            notfication.setVisibility(View.VISIBLE);
        }
        else {
            notfication.setVisibility(View.INVISIBLE);

        }

    }

  /*  public void clear() {
        notfication.setVisibility(View.INVISIBLE);
        ConnStatesCallBack.getInstance().getPrefManager().clearNotfication(Config.KEY_NEWS_FEED_NOTIFICATIONS);
    }*/
}
