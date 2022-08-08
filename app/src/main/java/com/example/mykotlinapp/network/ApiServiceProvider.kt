package com.example.mykotlinapp.network

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration

object ApiServiceProvider {

    const val BASE_URL = "http://10.0.2.2:3000"

    fun getRetrofitService(moshi: Moshi): ApiService {

        val okHttpClient = OkHttpClient()
            .newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .callTimeout(Duration.ofSeconds(7))
            .readTimeout(Duration.ofSeconds(7))
            .writeTimeout(Duration.ofSeconds(7))
            .build()

        /**
         * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
         * object.
         */
        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }


}