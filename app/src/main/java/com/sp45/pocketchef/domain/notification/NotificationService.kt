package com.sp45.pocketchef.domain.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sp45.pocketchef.R
import java.util.Calendar
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService @Inject constructor(
    private val context: Context
) {
    companion object {
        const val DAILY_REMINDER_ID = 1001
        const val COOKING_TIP_ID = 1002
        const val CHANNEL_ID = "pocketchef_channel"
        const val CHANNEL_NAME = "PocketChef Notifications"

        // Cooking tips for random selection
        private val cookingTips = listOf(
            "Let meat rest after cooking - it stays juicier! 🥩",
            "Salt pasta water like the sea for perfect pasta! 🌊",
            "Fresh herbs at the end = maximum flavor! 🌿",
            "Sharp knives are safer than dull ones! 🔪",
            "Taste as you cook - be the master chef! 👨‍🍳",
            "Room temp ingredients mix better! 🥚",
            "Don't crowd the pan - get that perfect sear! 🍳",
            "Rest your meat for juicier results! 🥩",
            "Season in layers for deeper flavor! 🧂",
            "Preheat your pans for better cooking! 🔥"
        )
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            ).apply {
                description = "Cooking tips, recipe reminders, and cooking notifications"
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleDailyReminder(hour: Int = 18, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = "DAILY_RECIPE_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun scheduleRandomCookingTips() {
        // Schedule random cooking tips throughout the day
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = "COOKING_TIP_ALERT"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            COOKING_TIP_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for random time between 10 AM and 8 PM
        val random = Random()
        val hour = 10 + random.nextInt(10) // 10 AM to 8 PM
        val minute = random.nextInt(60)

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelDailyReminder() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun cancelCookingTips() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            COOKING_TIP_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    fun showCookingTipNotification(tip: String? = null) {
        createNotificationChannel()

        val cookingTip = tip ?: cookingTips.random()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🍳 PocketChef Cooking Tip")
            .setContentText(cookingTip)
            .setSmallIcon(R.drawable.pocketchef_app_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cookingTip))
            .build()

        NotificationManagerCompat.from(context).notify(COOKING_TIP_ID, notification)
    }

    fun showDailyReminderNotification() {
        createNotificationChannel()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("🍽️ Time to Cook!")
            .setContentText("Check out new recipe ideas in PocketChef")
            .setSmallIcon(R.drawable.pocketchef_app_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(DAILY_REMINDER_ID, notification)
    }

    fun getRandomCookingTip(): String {
        return cookingTips.random()
    }
}