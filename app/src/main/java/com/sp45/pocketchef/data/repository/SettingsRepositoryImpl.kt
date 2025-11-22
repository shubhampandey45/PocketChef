package com.sp45.pocketchef.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.sp45.pocketchef.domain.AppSettings
import com.sp45.pocketchef.domain.ThemeMode
import com.sp45.pocketchef.domain.repository.SettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DAILY_REMINDERS = booleanPreferencesKey("daily_reminders")
        private val COOKING_TIPS = booleanPreferencesKey("cooking_tips")
        private val LAST_CACHE_CLEAR = longPreferencesKey("last_cache_clear")
    }

    override fun getAppSettings(): Flow<AppSettings> {
        return context.settingsDataStore.data.map { preferences ->
            AppSettings(
                theme = ThemeMode.valueOf(
                    preferences[THEME_MODE] ?: ThemeMode.SYSTEM.name
                ),
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                dailyReminders = preferences[DAILY_REMINDERS] ?: true,
                cookingTips = preferences[COOKING_TIPS] ?: true,
                lastCacheClear = preferences[LAST_CACHE_CLEAR] ?: 0L
            )
        }
    }

    override suspend fun updateTheme(theme: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[THEME_MODE] = theme.name
        }
    }

    override suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    override suspend fun updateDailyReminders(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DAILY_REMINDERS] = enabled
        }
    }

    override suspend fun updateCookingTips(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[COOKING_TIPS] = enabled
        }
    }

    override suspend fun updateLastCacheClear(timestamp: Long) {
        context.settingsDataStore.edit { preferences ->
            preferences[LAST_CACHE_CLEAR] = timestamp
        }
    }
}