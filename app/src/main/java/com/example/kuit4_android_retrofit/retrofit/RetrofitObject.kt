package com.example.kuit4_android_retrofit.retrofit

import com.example.kuit4_android_retrofit.BuildConfig
import com.example.kuit4_android_retrofit.BuildConfig.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {

    // 슬래쉬 꼭 붙이기 조심
    // 로컬프로펄티
    // 로컬로 숨기기

    val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}

class BuildConfig {

}
