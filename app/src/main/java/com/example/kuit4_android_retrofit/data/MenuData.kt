package com.example.kuit4_android_retrofit.data

import androidx.room.Entity

@Entity
data class MenuData(
    val menuName: String,
    val menuImgUrl: String,
    val rating: Double,
    val eta: Int,
    val id: String
)
