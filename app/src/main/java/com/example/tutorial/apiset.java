package com.example.tutorial;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface apiset
{
    @FormUrlEncoded
    @POST("register.php")
    Call<responsemodel> getregister(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

}
