package com.example.kuit4_android_retrofit.data

data class MenuData(
    val menuImg:String,
    val menuName: String,
    val menuTime: Int,// 이것도 int로 바꿔서 string으로 text 2개로 데이터 바인딩하기
    val menuRate: Double,//
    val id: String
)
