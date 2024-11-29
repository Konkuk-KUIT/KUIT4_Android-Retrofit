package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.PopularMenuData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PopularMenuService {
    @GET("popularmenu")
    fun getPopularMenu(): Call<List<PopularMenuData>>

    @POST("popularmenu")
    fun postMenu(@Body menuData: PopularMenuData): Call<PopularMenuData>

    @PUT("popularmenu/{id}")
    fun putMenu(@Path("id") id: String, @Body menuData: PopularMenuData): Call<PopularMenuData>

    @DELETE("popularmenu/{id}")
    fun deleteMenu(@Path("id") id: String): Call<Void>
}