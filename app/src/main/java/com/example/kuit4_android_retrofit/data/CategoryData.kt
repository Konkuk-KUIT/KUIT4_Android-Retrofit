package com.example.kuit4_android_retrofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// RoomDB 로 사용하는 게 아니므로 @Entity 삭제, 기본키 삭제
data class CategoryData(
    val categoryName: String,
    val categoryImg: String,
    //@PrimaryKey(autoGenerate = true) val id: Int = 0,
)
