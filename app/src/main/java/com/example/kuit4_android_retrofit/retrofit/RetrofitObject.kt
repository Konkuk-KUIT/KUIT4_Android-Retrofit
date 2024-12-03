package com.example.kuit4_android_retrofit.retrofit
import com.example.kuit4_android_retrofit.BuildConfig.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    // 마지막 슬래쉬까지 꼭 붙여야함
    val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}