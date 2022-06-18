package com.dicoding.naim.githubuserapp2.api

import com.dicoding.naim.githubuserapp2.BuildConfig
import com.dicoding.naim.githubuserapp2.response.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Headers("Authorization: token " + BuildConfig.GITHUBAPIKEY)
    @GET("search/users")
    fun getUser(
        @Query("q") username: String,
        @Query("per_page") per_page: Int = 10,
        @Query("page") page: Int = 1
    ): Call<UserResponse>?

    @Headers("Authorization: token " + BuildConfig.GITHUBAPIKEY)
    @GET("users/{username}")
    fun getDetailUser(
        @Path("username") username: String
    ): Call<DetailUserResponse>?

    @Headers("Authorization: token " + BuildConfig.GITHUBAPIKEY)
    @GET("users/{username}/followers")
    fun getFollowersUser(
        @Path("username") username: String
    ): Call<List<ItemsItem>>?

    @Headers("Authorization: token " + BuildConfig.GITHUBAPIKEY)
    @GET("users/{username}/following")
    fun getFollowingUser(
        @Path("username") username: String
    ): Call<List<ItemsItem>>?
}