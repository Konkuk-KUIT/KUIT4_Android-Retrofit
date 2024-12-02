package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.MenuData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PopularService {
    @GET("menu")
    fun getMenus(): Call<List<MenuData>>

    @POST("menu")
    fun postMenu(
        @Body menuData: MenuData
    ): Call<MenuData>

    @DELETE("menu/{id}")
    fun deleteMenu(
        @Path("id") id: String,
    ): Call<Void>

    @PUT("menu/{id}")
    fun putMenu(
        @Path("id") id:String,
        @Body updateCategoryData: MenuData,
    ):Call<MenuData>
}