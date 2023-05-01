package com.lucasginard.airelibre.modules.notifications.data

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.notifications.NotificationManager
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServiceSensor : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val apiService = BaseCallService.serviceSensor().create(APINotification::class.java)
        val sourceSensor = intent?.getStringExtra(Constants.SOURCE_SENSOR) ?: ""

        GlobalScope.launch {
            val response = apiService.getSensor(Utils.getISODate(),sourceSensor)
            if (response.isSuccessful){
                onResult(response.body()?.first(),applicationContext)
            }
        }
        stopSelf()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun onResult(sensor: SensorResponse?, context: Context){
        val notification = sensor?.let {
            NotificationManager(context).showNotification(
                "Sensor: ${sensor.description}",
                "Contenido de la notificación",
                it.source
            )
        }

        sensor?.id = 1
        sensor?.id?.let { id ->
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.notify(id, notification)
            return
        }
    }
}