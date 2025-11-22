package com.sp45.pocketchef.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sp45.pocketchef.data.local.ingredient.Ingredient
import com.sp45.pocketchef.data.local.ingredient.IngredientDao
import com.sp45.pocketchef.data.local.recipe.RecipeDao
import com.sp45.pocketchef.data.local.recipe.RecipeEntity

@Database(entities = [Ingredient::class, RecipeEntity::class], version = 2, exportSchema = false)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
}