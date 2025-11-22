package com.sp45.pocketchef.domain

data class AppSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val dailyReminders: Boolean = true,
    val cookingTips: Boolean = true,
    val lastCacheClear: Long = 0L
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}