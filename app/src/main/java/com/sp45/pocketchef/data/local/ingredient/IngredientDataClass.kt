package com.sp45.pocketchef.data.local.ingredient

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

// Room Entity representing a single ingredient item
@Entity(tableName = "ingredients")
data class Ingredient(
    // Using UUID for a unique primary key across the app (local and Firestore)
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val addedTime: Long = System.currentTimeMillis()
)