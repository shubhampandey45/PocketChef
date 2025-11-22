package com.sp45.pocketchef.domain.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DailyReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            "DAILY_RECIPE_REMINDER" -> {
                notificationService.showDailyReminderNotification()
                // Reschedule for next day
                notificationService.scheduleDailyReminder()
            }
            "COOKING_TIP_ALERT" -> {
                notificationService.showCookingTipNotification()
                // Reschedule for next random time
                notificationService.scheduleRandomCookingTips()
            }
        }
    }
}