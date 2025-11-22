package com.sp45.pocketchef.data.local.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import java.util.UUID

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val prepTime: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val nutritionalInfo: String = "",

    // This field is for local cache management [cite: 86]
    @get:Exclude // Exclude from Firestore
    val cachedTime: Long = System.currentTimeMillis()
)