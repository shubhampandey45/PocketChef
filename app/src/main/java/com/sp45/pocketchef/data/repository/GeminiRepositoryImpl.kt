package com.sp45.pocketchef.data.repository

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.sp45.pocketchef.data.local.recipe.RecipeEntity
import com.sp45.pocketchef.domain.repository.GeminiRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepositoryImpl @Inject constructor() : GeminiRepository {

    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-2.5-flash-lite"
        )
    }

    override suspend fun getCookingTip(): String = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Generate a short, fun cooking tip or food fact. 
                Make it practical and useful for home cooks.
                Keep it under 120 characters.
                Make it engaging and include an emoji if appropriate.
                Examples:
                - "Let meat rest after cooking - it stays juicier! 🥩"
                - "Salt pasta water like the sea for perfect pasta! 🌊"
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            response.text ?: getDemoCookingTip()

        } catch (e: Exception) {
            e.printStackTrace()
            getDemoCookingTip()
        }
    }

    private fun getDemoCookingTip(): String {
        val demoTips = listOf(
            "Fresh herbs at the end = maximum flavor! 🌿",
            "Sharp knives are safer than dull ones! 🔪",
            "Taste as you cook - be the master chef! 👨‍🍳",
            "Room temp ingredients mix better! 🥚",
            "Don't crowd the pan - get that perfect sear! 🍳",
            "Rest your meat for juicier results! 🥩",
            "Season in layers for deeper flavor! 🧂"
        )
        return demoTips.random()
    }

    private val gson = Gson()

    override suspend fun getIngredientSuggestions(query: String): List<String> =
        withContext(Dispatchers.IO) {
            if (query.length < 2) return@withContext emptyList()

            try {
                val prompt = """
                Based on the partial input "$query", generate a list of 5 common food ingredient suggestions.
                Return the list *only* as a valid JSON array of strings.
                Example: ["Tomato", "Tomato Paste", "Tomato Sauce", "Tomatillo", "Cherry Tomato"]
            """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text ?: return@withContext emptyList()

                // Improved JSON extraction
                val jsonStart = responseText.indexOf("[")
                val jsonEnd = responseText.lastIndexOf("]")

                if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                    return@withContext emptyList()
                }

                val jsonArrayString = responseText.substring(jsonStart, jsonEnd + 1)

                // Parse the JSON array string
                val listType = object : TypeToken<List<String>>() {}.type
                return@withContext gson.fromJson(jsonArrayString, listType)

            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback for demo purposes
                val demoSuggestions = listOf("Chicken", "Cheese", "Carrot", "Cream", "Cilantro")
                return@withContext demoSuggestions.filter { it.contains(query, ignoreCase = true) }
            }
        }

    override suspend fun getRecipes(ingredients: List<String>): List<RecipeEntity> =
        withContext(Dispatchers.IO) {
            val ingredientString = ingredients.joinToString(", ")

            try {
                val prompt = """
            You are a helpful culinary assistant. Based on the ingredients: "$ingredientString", generate 3 recipe ideas.

            IMPORTANT: Return ONLY a valid JSON array (a list) of objects. Do not include any other text or markdown.

            Each object in the array must have the following exact structure:
            {
              "name": "Recipe Name",
              "prepTime": "Estimated Prep Time (e.g., '30 minutes')",
              "ingredients": ["List", "of", "required", "ingredients"],
              "instructions": ["Step 1", "Step 2", "Step 3"],
              "nutritionalInfo": "Brief overview (e.g., 'Approx. 450 calories per serving.')"
            }

            Example:
            [
              {
                "name": "Spinach and Egg Scramble",
                "prepTime": "10 minutes",
                "ingredients": ["2 Eggs", "1 cup Spinach", "1 tbsp Milk", "Salt", "Pepper"],
                "instructions": ["Whisk eggs, milk, salt, and pepper.", "Sauté spinach in a pan.", "Add egg mixture and scramble until cooked."],
                "nutritionalInfo": "Approx. 200 calories."
              }
            ]
        """.trimIndent()

                println("Sending prompt to Gemini: $prompt")

                val response = generativeModel.generateContent(prompt)
                val responseText = response.text
                    ?: return@withContext emptyList()

                println("Raw Gemini response: $responseText")

                // Improved JSON extraction with better error handling
                val jsonStart = responseText.indexOf("[")
                val jsonEnd = responseText.lastIndexOf("]")

                if (jsonStart == -1 || jsonEnd == -1 || jsonEnd <= jsonStart) {
                    println("No valid JSON array found in response")
                    return@withContext emptyList()
                }

                val jsonArrayString = responseText.substring(jsonStart, jsonEnd + 1)
                println("Extracted JSON: $jsonArrayString")

                // Parse the JSON array
                val listType = object : TypeToken<List<RecipeEntity>>() {}.type
                val recipes = gson.fromJson<List<RecipeEntity>>(jsonArrayString, listType)

                println("Successfully parsed ${recipes.size} recipes")
                return@withContext recipes

            } catch (e: JsonSyntaxException) {
                println("JSON parsing error: ${e.message}")
                e.printStackTrace()
                return@withContext emptyList()
            } catch (e: Exception) {
                println("Gemini Recipe Error: ${e.message}")
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
}