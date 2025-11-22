package com.sp45.pocketchef.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.sp45.pocketchef.data.local.AppDatabase
import com.sp45.pocketchef.data.local.ingredient.IngredientDao
import com.sp45.pocketchef.data.local.recipe.RecipeDao
import com.sp45.pocketchef.data.repository.AuthRepositoryImpl
import com.sp45.pocketchef.data.repository.GeminiRepositoryImpl
import com.sp45.pocketchef.data.repository.IngredientRepositoryImpl
import com.sp45.pocketchef.data.repository.RecipeRepositoryImpl
import com.sp45.pocketchef.data.repository.SettingsRepositoryImpl
import com.sp45.pocketchef.data.repository.UserRepositoryImpl
import com.sp45.pocketchef.domain.notification.NotificationService
import com.sp45.pocketchef.domain.repository.AuthRepository
import com.sp45.pocketchef.domain.repository.GeminiRepository
import com.sp45.pocketchef.domain.repository.IngredientLocalRepository
import com.sp45.pocketchef.domain.repository.IngredientRepository
import com.sp45.pocketchef.domain.repository.RecipeRepository
import com.sp45.pocketchef.domain.repository.SettingsRepository
import com.sp45.pocketchef.domain.repository.UserRepository
import com.sp45.pocketchef.domain.theme.ThemeManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore
    ): UserRepository = UserRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideGeminiRepository(): GeminiRepository = GeminiRepositoryImpl()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pocketchef_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideIngredientDao(appDatabase: AppDatabase): IngredientDao {
        return appDatabase.ingredientDao()
    }

    @Provides
    @Singleton
    fun provideIngredientRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        ingredientDao: IngredientDao,
        localRepository: IngredientLocalRepository // Specify FQN if needed
    ): IngredientRepository = IngredientRepositoryImpl(
        auth, firestore, ingredientDao, localRepository
    )

    @Provides
    @Singleton
    fun provideIngredientLocalRepository(
        @ApplicationContext context: Context,
        ingredientDao: IngredientDao
    ): IngredientLocalRepository = IngredientLocalRepository(context, ingredientDao)

    @Provides
    @Singleton
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        return appDatabase.recipeDao()
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        recipeDao: RecipeDao
    ): RecipeRepository = RecipeRepositoryImpl(auth, firestore, recipeDao)

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository =
        SettingsRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideNotificationService(@ApplicationContext context: Context): NotificationService =
        NotificationService(context)

    @Provides
    @Singleton
    fun provideThemeManager(settingsRepository: SettingsRepository): ThemeManager =
        ThemeManager(settingsRepository)
}