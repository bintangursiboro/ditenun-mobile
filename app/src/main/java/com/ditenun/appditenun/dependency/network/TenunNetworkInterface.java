package com.ditenun.appditenun.dependency.network;

import com.ditenun.appditenun.dependency.models.ResponseGenerateFile;
import com.ditenun.appditenun.dependency.models.ResponseGetData;
import com.ditenun.appditenun.dependency.models.ResponseGetDataMotif;
import com.ditenun.appditenun.dependency.models.ResponseGetDataMotifTenun;
import com.ditenun.appditenun.dependency.models.ResponseGetFaq;
import com.ditenun.appditenun.dependency.models.ResponseGetFeedback;
import com.ditenun.appditenun.dependency.models.ResponseGetUser;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface TenunNetworkInterface {

    @POST("api/login")
    Call<ResponseGetUser> login(
            @Header("Authorization") String token,
            @Query("username") String username,
            @Query("password") String password
    );

    @POST("api/register")
    Call<ResponseGetUser> register(
            @Header("Authorization") String token,
            @Query("name") String name,
            @Query("email") String email,
            @Query("password") String password,
            @Query("no_hp") String no_hp,
            @Query("alamat") String alamat,
            @Query("jenis_tenun") String jenis_tenun
    );

    @POST("api/postFeedback")
    Call<ResponseGetFeedback> feedback(
            @Header("Authorization") String token,
            @Query("subjek") String subjek,
            @Query("deskripsi") String deskripsi,
            @Query("rating") Integer rating,
            @Query("user_id") Integer user_id
    );

    @GET("api/viewAllFaq")
    Call<ResponseGetFaq> getListFaq(
            @Header("Authorization") String token
    );

    @GET("api/tenun")
    Call<ResponseGetData> getListTenun(@Header("Authorization") String token, @Query("cursor") int cursor, @Query("size") String size);

    @GET("api/motifTenun")
    Call<ResponseGetDataMotif> getListMotif(@Header("Authorization") String token, @Query("cursor") int cursor, @Query("size") String size);

    @Multipart
    @POST("api/uploadImage")
    Call<ResponseGetDataMotifTenun> uploadImage(
            @Header("Authorization") String token,
            @Part("nama_motif") String nama_motif,
            @Part("id_tenun") int id_tenun,
            @Part MultipartBody.Part photo
    );

    @Multipart
    @POST("api/kristik-to-edit")
    Call<ResponseBody> kristikEditor(
            @Header("Authorization") String token,
            @Part("square_size") int squareSize,
            @Part("color_amount") int colorAmount,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("api/kristiks")
    Call<ResponseBody> hasilKristik(
            @Header("Authorization") String token,
            @Part("square_size") int squareSize,
            @Part("color_amount") int colorAmount,
            @Part MultipartBody.Part file
    );

    @Multipart
    @POST("api/motif")
    Call<ResponseBody> generateMotif(
            @Header("Authorization") String token,
            @Part("matrix") int matix,
            @Part("color") int color,
            @Part MultipartBody.Part file
    );

    @GET
    Call<ResponseBody> getTentunImage(@Url String url);

    @GET("api/listGenerate")
    Call<ResponseGenerateFile> getListGenerate(@Header("Authorization") String token, @Query("cursor") int cursor, @Query("size") String size);
}
