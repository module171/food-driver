package com.FoodDriver.app.api

import com.FoodDriver.app.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("driverlogin")
    fun getLogin(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>

    @POST("drivergetprofile")
    fun getProfile(@Body map: HashMap<String, String>): Call<RestResponse<ProfileModel>>

    @Multipart
    @POST("drivereditprofile")
    fun setProfile(@Part("user_id") userId: RequestBody,@Part("name") name: RequestBody, @Part profileimage: MultipartBody.Part?): Call<SingleResponse>

    @POST("driverchangepassword")
    fun setChangePassword(@Body map: HashMap<String, String>):Call<SingleResponse>

    @POST("driverforgotPassword")
    fun setforgotPassword(@Body map: HashMap<String, String>):Call<SingleResponse>

    @POST("driverorder")
    fun getOrderHistory(@Body map: HashMap<String, String>):Call<ListResponceDriverOrder>

    @POST("driverorderdetails")
    fun setgetOrderDetail(@Body map: HashMap<String, String>):Call<RestOrderDetailResponse>

    @POST("delivered")
    fun setOrderDeliver(@Body map: HashMap<String, String>):Call<SingleResponse>

    @POST("driverongoingorder")
    fun setOnGoingOrder(@Body map: HashMap<String, String>):Call<ListResponceDriverOrder>
}