package com.titfer.Activties.Service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by sotra on 5/10/2017.
 */
public interface UploadFileService  {
    @Headers("UserModel-Agent: android")
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file,
            @Part("name") RequestBody name
    );
}
