package com.lucasginard.airelibre.modules.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.home.view.MainActivity
import com.lucasginard.airelibre.utils.Constants

class NotificationManager(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, description: String,idSensorNotifiy:String):Notification {
        // Crear un canal de notificación (solo necesario en Android 8 y versiones posteriores)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = Constants.CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(idSensorNotifiy, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
        val mainIntent = Intent(context, MainActivity::class.java)
        mainIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_MUTABLE)

        // Crea la notificación
        val builder = NotificationCompat.Builder(context, idSensorNotifiy)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(description)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return  builder.build()
    }

    fun getAllNotificationsEnables():ArrayList<Int>{
        val activeNotifications = notificationManager.activeNotifications
        val idList = ArrayList<Int>()
        activeNotifications.forEach{notification ->
            idList.add(notification.id)
        }
        return idList
    }
}