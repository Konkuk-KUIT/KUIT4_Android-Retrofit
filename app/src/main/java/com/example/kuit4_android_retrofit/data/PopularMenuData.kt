package com.example.kuit4_android_retrofit.data

import androidx.room.Entity

@Entity
data class PopularMenuData(
    val popularMenuName: String,
    val popularMenuImg: String,
    val popularMenuTime: Int,
    val popularMenuRating: Double
)
