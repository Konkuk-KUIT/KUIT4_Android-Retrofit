package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import com.example.kuit4_android_retrofit.data.MenuData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PopularMenuService {
    @GET("popular") //() 안에 endpoint의 값 넣어서 사용
    fun getCategories(): Call<List<MenuData>>

    @POST("popular")
    fun postPopular(
        @Body popularMEnuData: MenuData
    ): Call<MenuData>

    @DELETE("popular/{id}")
    fun deletePopular(
        @Path("id") id: String,
    ): Call<Void>

    @PUT("popular/{id}")
    fun putPopular(
        @Path("id") id: String,
        @Body updatedPopularMenuData: MenuData
    ): Call<MenuData>
}