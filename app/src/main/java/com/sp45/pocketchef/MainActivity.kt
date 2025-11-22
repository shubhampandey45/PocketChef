package com.sp45.pocketchef

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.sp45.pocketchef.presentation.ui.theme.PocketChefTheme
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import com.sp45.pocketchef.domain.AppSettings
import com.sp45.pocketchef.domain.notification.NotificationService
import com.sp45.pocketchef.domain.theme.ThemeManager
import com.sp45.pocketchef.presentation.navigation.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        createNotificationChannel()

        setContent {
            val appSettings by themeManager.getAppSettings().collectAsState(initial = AppSettings())

            LaunchedEffect(appSettings.theme) {
                themeManager.updateTheme(appSettings.theme)
            }

            PocketChefApp(themeManager = themeManager)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PocketChef Notifications"
            val descriptionText = "Cooking tips, recipe reminders, and cooking notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(
                NotificationService.CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun PocketChefApp(themeManager: ThemeManager) {
    val darkTheme = themeManager.shouldUseDarkTheme()

    PocketChefTheme() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph()
        }
    }
}