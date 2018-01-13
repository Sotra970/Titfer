package com.titfer.FCM;


import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by lenovo on 6/30/2017.
 */

public interface FCM_SERVICE {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyBcJ3qDqNv62Ick1oLV4B7gUxgI6dqZx64"
    })
    @POST("/fcm/send")
    Call<JSONObject> send_chat_message(
            @Body FCM_CHAT_MESSAGE_OBJ fcm_service_obj
    );



    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyBcJ3qDqNv62Ick1oLV4B7gUxgI6dqZx64"
    })
    @POST("/fcm/send")
    Call<JSONObject> send_notification(
            @Body FCM_Notfication_OBJ fcm_notfication_obj
    );


}
