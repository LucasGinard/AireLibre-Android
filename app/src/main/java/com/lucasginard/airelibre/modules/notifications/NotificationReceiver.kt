package com.lucasginard.airelibre.modules.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Construye la notificación utilizando NotificationUtil
        val notification = NotificationManager(context).showNotification(
            "Título de la notificación",
            "Contenido de la notificación",
            "1"
        )

        // Muestra la notificación
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify("1".toInt(), notification)
    }

}