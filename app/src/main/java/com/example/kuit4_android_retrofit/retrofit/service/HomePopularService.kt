package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.PopularData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HomePopularService {
    @GET("popular")
    fun getPopular(): Call<List<PopularData>>

    @POST("popular")
    fun postPopular(
        @Body popularData: PopularData
    ): Call<PopularData>

    @DELETE("popular/{id}")
    fun deletePopular(
        @Path("id") id: String,
    ): Call<Void>

    @PUT("popular/{id}")
    fun putPopular(
        @Path("id") id: String,
        @Body updatedPopularData: PopularData,
    ): Call<PopularData>

}