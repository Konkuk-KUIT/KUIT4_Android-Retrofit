package com.example.kuit4_android_retrofit.retrofit

import com.example.kuit4_android_retrofit.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    //BuildConfig에서 가져와서 사용
    private const val BASE_URL = BuildConfig.BASE_URL

    val retrofit: Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}