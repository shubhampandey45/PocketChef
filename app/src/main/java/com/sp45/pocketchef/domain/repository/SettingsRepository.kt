package com.sp45.pocketchef.domain.repository

import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.domain.AppSettings
import com.sp45.pocketchef.domain.ThemeMode

interface SettingsRepository {
    fun getAppSettings(): Flow<AppSettings>
    suspend fun updateTheme(theme: ThemeMode)
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    suspend fun updateDailyReminders(enabled: Boolean)
    suspend fun updateCookingTips(enabled: Boolean)
    suspend fun updateLastCacheClear(timestamp: Long)
}