package com.v1.foundspecs.milzam.skripsiapp.Interface;

import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Created by Milzam 10/28/2017.
 */

public interface ApiMethodInterface {
    @FormUrlEncoded
    @POST
    Call<ServerResponse> login(@Url String url, @Field("email") String user, @Field("password") String pass);

    @FormUrlEncoded
    @POST
    Call<ServerResponse>register(@Url String url,
                                 @Field("email") String email,
                                 @Field("name") String name,
                                 @Field("password") String password,
                                 @Field("phone_number") String ponsel,
                                 @Field("address") String address);

    @FormUrlEncoded
    @POST
    Call<ServerResponse> refresh(@Url String url, @Field("refresh_token") String refresh_token);

    @FormUrlEncoded
    @POST
    Call<ServerResponse> reset(@Url String url, @Field("email") String email,@Field("password") String password);

    @POST
    Call<ServerResponse>send(@Url String url, @Header("Accept") String accept, @Header("Authorization") String authorization);

    @FormUrlEncoded
    @POST
    Call<ServerResponse>sendPassword(
            @Url String url,
            @Header("Accept") String accept,
            @Header("Authorization") String authorization,
            @Field("password") String complaint_id
    );

    @FormUrlEncoded
    @POST
    Call<ServerResponse>sendData(
            @Url String url,
            @Header("Accept") String accept,
            @Header("Authorization") String authorization,
            @Field("email") String complaint_id,
            @Field("name")String user_id,
            @Field("phone_number")String phone_number,
            @Field("address")String addres
    );

    @FormUrlEncoded
    @POST
    Call<ServerResponse>checkEmail(
            @Url String url,
            @Field("email") String email
    );

    @Multipart
    @POST
    Call<ServerResponse>uploadImage(@Url String url,
                                    @Header("Accept") String accept,
                                    @Header("Authorization") String authorization,
                                    @Part MultipartBody.Part image,
                                    @Part("category_id") RequestBody category_id,
                                    @Part("address") RequestBody address,
                                    @Part("city") RequestBody city,
                                    @Part("subdistrict") RequestBody subdistrict,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("lattitude") RequestBody lattitude,
                                    @Part("description") RequestBody description);
}
