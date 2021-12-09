package com.example.internbookchor;

import android.database.Observable;

import com.google.gson.JsonElement;

import java.io.File;
import java.util.Base64;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("upload_image.php")
    Call<Bookchor> uploadImage(@Field("image_path") String image);

    @FormUrlEncoded
    @POST("upload_image.php")
    Call<Bookchor> imageUpload(@Field("image_path") String image
    );

    @FormUrlEncoded
    @POST("upload_image.php")
    Call<Bookchor> longUpload(@Field("longitude") Double longitude
    );

    @FormUrlEncoded
    @POST("upload_image.php")
    Call<Bookchor> latUpload(@Field("latitude") Double lat
    );




}
