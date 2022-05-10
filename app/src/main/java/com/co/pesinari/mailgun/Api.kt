package com.co.pesinari.mailgun

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {
    @FormUrlEncoded
    @POST("messages")
    fun sendEmail(
        @Field("from") from: String?,
        @Field("to") to: String?,
        @Field("subject") subject: String?,
        @Field("text") text: String?
    ): Call<ResponseBody?>?
}