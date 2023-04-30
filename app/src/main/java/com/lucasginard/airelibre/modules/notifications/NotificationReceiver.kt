package com.lucasginard.airelibre.modules.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.utils.Constants

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val stringSensor = intent.getStringExtra(Constants.OBJECT_SENSOR)
        val sensor = Gson().fromJson(stringSensor,SensorResponse::class.java)

        val notification = NotificationManager(context).showNotification(
            "Sensor: ${sensor.description}",
            "Contenido de la notificación",
            sensor.source
        )

        sensor.id?.let { id ->
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.notify(id, notification)
            return
        }
        //val notificationManager = NotificationManagerCompat.from(context)
        //notificationManager.cancel(sensor.id)
    }

}