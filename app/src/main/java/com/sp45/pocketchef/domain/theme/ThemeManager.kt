package com.sp45.pocketchef.domain.theme

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.Flow
import com.sp45.pocketchef.domain.ThemeMode
import com.sp45.pocketchef.domain.repository.SettingsRepository
import com.sp45.pocketchef.domain.AppSettings
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    var currentTheme by mutableStateOf(ThemeMode.SYSTEM)
        private set

    suspend fun updateTheme(theme: ThemeMode) {
        settingsRepository.updateTheme(theme)
        currentTheme = theme
    }

    fun getAppSettings(): Flow<AppSettings> {
        return settingsRepository.getAppSettings()
    }

    @Composable
    fun shouldUseDarkTheme(): Boolean {
        val context = LocalContext.current
        return when (currentTheme) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
    }
}