package com.co.pesinari.mailgun

import android.util.Base64
import android.util.Log
import com.co.pesinari.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient private constructor() {
    private val client: Retrofit
    val api: Api
        get() = client.create(Api::class.java)

    companion object {
        private var BASE_URL =
            BuildConfig.MAILGUN_BASE_URL
        private const val API_USERNAME = "api"

        private var API_PASSWORD = BuildConfig.MAILGUN_API_KEY

        private val AUTH = "Basic " + Base64.encodeToString(
            ("$API_USERNAME:$API_PASSWORD").toByteArray(),
            Base64.NO_WRAP
        )
        private var mInstance: RetrofitClient? = null

        @get:Synchronized
        val instance: RetrofitClient?
            get() {
                if (mInstance == null) {
                    mInstance = RetrofitClient()
                }
                return mInstance
            }

    }

    init {
        val okClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()

                //Adding basic auth
                val requestBuilder = original.newBuilder()
                    .header("Authorization", AUTH)
                    .method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
        client = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okClient)
            .build()
    }


}
