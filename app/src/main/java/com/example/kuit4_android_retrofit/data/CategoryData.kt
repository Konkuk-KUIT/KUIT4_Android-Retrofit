package com.example.kuit4_android_retrofit.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryData(
    val categoryName: String,
    val categoryImg: String,
    val id: String
)
