package com.sp45.pocketchef.data.model

data class Recipe(
    val name: String = "",
    val prepTime: String = "",
    val ingredients: List<String> = emptyList(),
    val instructions: List<String> = emptyList(),
    val nutritionalInfo: String = ""
)