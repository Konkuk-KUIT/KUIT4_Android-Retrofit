package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface CategoryService {
    @GET("category")
    fun getCategories(): Call<List<CategoryData>>

    @POST("category")
    fun postCategory(
        @Body categoryData: CategoryData
    ): Call<CategoryData> // 카테고리 데이터 담아서 전송 후 응답 받기

    @DELETE("category/{id}") // id에 해당하는 카테고리 삭제
    fun deleteCategory(
        @Path("id") id: String,
    ): Call<Void>

    @PUT("category/{id}")
    fun putCategory(
        @Path("id") id: String,
        @Body updatedCategoryData: CategoryData,
    ): Call<CategoryData>
}