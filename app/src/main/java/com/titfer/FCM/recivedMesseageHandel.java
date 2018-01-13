package com.titfer.FCM;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.RemoteMessage;
import com.titfer.Activties.cahting.MessagesTabsActivity;
import com.titfer.Models.NotificationModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.app.Config;
import com.titfer.app.MyPreferenceManager;
import com.titfer.internal_db.Mess_tabel;
import com.titfer.internal_db.Room_tabel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

/**
 * Created by Sotraa on 6/15/2016.
 */
public class recivedMesseageHandel {
    Context context ;
    int ID = 0 ;
    private NotificationTarget notificationTarget;

    public recivedMesseageHandel(Context context, RemoteMessage remoteMessage){
        this.context = context.getApplicationContext() ;

    try {

        if (remoteMessage.getData().get("action").equals("message")){
            Map<String, String> currentElemnt = remoteMessage.getData();
            //add messege
            //notfy room
            Room_tabel room_tabel = new Room_tabel();
            Mess_tabel mess =  new Mess_tabel() ;

            mess.user_id = currentElemnt.get("user_id") ;
            if (!mess.user_id.equals(AppContoller.getInstance().getPrefManager().getUser().getId())) {


                mess.mess_id = currentElemnt.get("mess_id") ;
                mess.room_id = currentElemnt.get("room_id") ;
                mess.message = currentElemnt.get("message") ;
                mess.timeStamp = currentElemnt.get("timeStamp") ;
                mess.user_name = currentElemnt.get("user_name") ;
                mess.dir = 1 ;
                if (!mess.check(currentElemnt.get("mess_id"))){
                mess.save() ;
                }

                    room_tabel.update_count(mess.room_id);
                    Intent intent1 = new Intent();
                    intent1.putExtra("room_id",mess.room_id);
                    intent1.setAction(Config.UPDATE_USER_CHAT_ICON);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent1);

                    Intent intent2 = new Intent();
                    intent2.putExtra("room_id",mess.room_id);
                intent2.setAction(Config.REFRESH_MESSAGES);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);

                    Log.e("incoming messsage  " ,"incoming message");
                if (!AppContoller.getInstance().getPrefManager().get_current_user().equals(mess.room_id))
                 sendFeedNotification(mess);


            }
        }


        if (remoteMessage.getData().get("action").equals("notification")){

            if (remoteMessage.getData().get("user_id").equals(AppContoller.getInstance().getPrefManager().getUser().getId())){
                sendNotification(new NotificationModel(
                        remoteMessage.getData().get("message") ,
                                remoteMessage.getData().get("date")
                ));
            }

            if (remoteMessage.getData().get("user_id").equals("admin") && AppContoller.getInstance().getPrefManager().getUser().getType() ==2 ){
                sendNotification(new NotificationModel(
                        remoteMessage.getData().get("message") ,
                        remoteMessage.getData().get("date")
                ));
            }
        }
    }catch (Exception e){
        Log.e("receive fcm exception",e.toString());
    }

    }



    private void sendFeedNotification(Mess_tabel mess) {
        Intent intent = new Intent(context, MessagesTabsActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(mess.user_name)
                .setContentText(mess.message)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority( NotificationCompat.PRIORITY_HIGH)
                ;


        final Notification notification = notificationBuilder.build();

        long id =  Calendar.getInstance().getTimeInMillis() ;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//        notificationManager.notify(Integer.parseInt(mess.room_id) /* ID of notification */, notification);
        notificationManager.notify(mess.room_id, (int) id,notification);
//
    }




    private void sendNotification(NotificationModel notificationModel) {
        inc_noti();
        Intent intent = new Intent(context, MessagesTabsActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Titfer")
                .setContentText(notificationModel.getMessage())
                .setSmallIcon(R.drawable.ic_stat_name)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority( NotificationCompat.PRIORITY_HIGH)
                ;


        final Notification notification = notificationBuilder.build();

        long id =  Calendar.getInstance().getTimeInMillis() ;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify((int) id,notification);
//
    }


    void inc_noti(){
        AppContoller.getInstance().getPrefManager().INCREMENT_NOTFICATiON();
        Intent intent = new Intent() ;
        intent.setAction(MyPreferenceManager.KEY_INCREMENT_NOTFICATiON);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}