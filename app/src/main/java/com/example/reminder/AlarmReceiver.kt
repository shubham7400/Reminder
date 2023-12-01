package com.example.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


class AlarmReceiver : BroadcastReceiver() {



    private val GROUP_MESSAGE: String = "TODOLIST"

    // Handles the broadcasted alarm intent
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager: NotificationManager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager


        println("dfdsfds came")

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            // Configure the notification channel.
            notificationChannel.description = "Sample Channel description"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Your Reminder")
            .setContentText("It is the reminder you have set for the task.")
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(Color.RED)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        // Notify the user with the created notification
        notificationManager.notify(getNumber(), notification)


    }


    // to show multiple number of notification , there is need of unique number
    private fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()

    companion object{
        private const val NOTIFICATION_CHANNEL_ID = "Remainder"
        private const val NOTIFICATION_NAME = "TODO Notifications"

    }

}