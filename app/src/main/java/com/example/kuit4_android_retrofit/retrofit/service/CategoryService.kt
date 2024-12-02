package com.example.kuit4_android_retrofit.retrofit.service

import com.example.kuit4_android_retrofit.data.CategoryData
import retrofit2.Call
import retrofit2.http.GET

interface CategoryService {
    @GET("category") // 엔드포인트 정의
    // API 호출할 때 쓸 메소드, 데이터 형식 지정
    fun getCategories(): Call<List<CategoryData>>
}