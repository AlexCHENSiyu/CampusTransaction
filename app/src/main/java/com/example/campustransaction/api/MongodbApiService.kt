package com.example.campustransaction.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

private const val BASE_URL = "http://10.0.2.2:5000"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface MongodbApiService {

    @GET("email-no-exist")
    suspend fun apiEmailNoExist(@Query("EmailAddress") EmailAddress:String): ResponseBasic

    @GET("email-validation")
    suspend fun apiEmailValidation(@Query("EmailAddress") EmailAddress:String,
                           @Query("InputCode") InputCode:Int?=null): ResponseBasic

    @Multipart
    @POST("set-reset-password")
    suspend fun apiSetPassword(@Part("EmailAddress") EmailAddress:String,
                       @Part("Password") Password:String): ResponseBasic

    @Multipart
    @POST("set-reset-password")
    suspend fun apiResetPassword(@Part("EmailAddress") EmailAddress:String,
                         @Part("Password") Password:String? = null,
                         @Part("NewPassword") NewPassword:String): ResponseBasic

    @POST("create-account")
    suspend fun apiCreateAccount(@Body UserInfo: RequestUserInfo): ResponseBasic

    @Multipart
    @POST("login")
    suspend fun apiLogin(@Part("EmailAddress") EmailAddress:String,
                 @Part("Password") Password:String): ResponseBasic

    @GET("get-user-info")
    suspend fun apiGetUserInfo(@Query("EmailAddress") EmailAddress:String): ResponseUserInfo

    @POST("send-message")
    suspend fun apiSendMessage(@Body Message: RequestMessage): ResponseBasic

    @Multipart
    @POST("get-message")
    suspend fun apiGetMessage(@Part("EmailAddress") EmailAddress:String,
                              @Part("Password") Password:String?,
                              @Part("Sender") Sender:String? = null): ResponseMessages

    @POST("new-post")
    suspend fun apiNewPost(@Body Post: RequestPost): ResponseBasic

    @Multipart
    @POST("delete-post")
    suspend fun apiDeletePost(@Part("EmailAddress") EmailAddress:String,
                              @Part("Password") Password:String?,
                              @Part("PID") PID:String): ResponseBasic

    @GET("user-posts")
    suspend fun apiUserPosts(@Query("EmailAddress") EmailAddress:String): ResponsePosts

    @Multipart
    @POST("post-comment")
    suspend fun apiPostComment(@Part("Commenter") EmailAddress:String,
                               @Part("PID") PID:String,
                               @Part("Text") Text:String): ResponseBasic

    @GET("get-posts")
    suspend fun apiGetPosts(@Query("EmailAddress") EmailAddress:String,
                            @Query("Keyword") Keyword:String? = null): ResponsePosts

    @GET("click-post")
    suspend fun apiClickPost(@Query("EmailAddress") EmailAddress:String,
                             @Query("PID") PID:String): ResponseBasic

    @GET("get-post-history")
    suspend fun apiPostHistory(@Query("EmailAddress") EmailAddress:String): ResponsePosts

    @POST("edit-post")
    suspend fun apiEditPost(@Body Post: RequestPost): ResponseBasic
    @Multipart
    @POST("parsing-pid")
    suspend fun apiParsingPid(@Part("PID") PID:String): ResponseBasic
    @POST("final-post")
    suspend fun apiFinalPost(): ResponseBasic
    @GET("old-post")
    suspend fun apiOldPost(@Query("PID") PID:String): ResponseBasicPost

    @Multipart
    @POST("true-edit-pid")
    suspend fun apiTeditPid(@Part("PID") PID:String,
                            @Part("Post") Post:RequestPost):ResponseBasic


}

object MongodbApi {
    val retrofitService : MongodbApiService by lazy { retrofit.create(MongodbApiService::class.java) }
}