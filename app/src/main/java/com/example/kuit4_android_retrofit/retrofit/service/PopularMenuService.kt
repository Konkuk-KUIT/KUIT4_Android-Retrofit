package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.PopularMenuData
import retrofit2.Call
import retrofit2.http.GET

interface PopularMenuService {
    @GET("popularmenu")
    fun getPopularMenu(): Call<List<PopularMenuData>>
}