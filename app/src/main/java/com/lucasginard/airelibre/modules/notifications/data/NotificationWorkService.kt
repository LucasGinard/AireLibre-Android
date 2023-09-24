package com.lucasginard.airelibre.modules.notifications.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.notifications.NotificationManager
import com.lucasginard.airelibre.modules.notifications.NotificationWorkManager
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.Utils
import com.lucasginard.airelibre.utils.getQualityAQI
import com.lucasginard.airelibre.utils.hexToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorkService(var context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val apiService = BaseCallService.serviceSensor().create(APINotification::class.java)

        val sourceSensor = inputData.getString(Constants.SOURCE_SENSOR) ?: ""
        val sensorIDNotify = inputData.getString(Constants.ID_SENSOR_NOTIFICATION) ?: ""
        val isPeriodicNotify = inputData.getBoolean(Constants.NOTIFICATION_SENSOR_IS_PERIODIC,false)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        val cancelNotification = {
            val notificationWorkerManager = NotificationWorkManager(context)
            if (isPeriodicNotify) {
                notificationWorkerManager.cancelPeriodicWork(sensorIDNotify)
            } else {
                AireLibreApp.prefs.cancelScheduledNotification(sensorIDNotify)
                notificationWorkerManager.cancelOneTimeNotification(sensorIDNotify)
            }
        }

        val showErrorNotify = {
            val notificationError = NotificationManager(applicationContext).showNotification(
                "😥 Error en la notificación de: $sensorIDNotify",
                "¡Ups! Parece que ha ocurrido un problema al procesar su notificación.",
                "Error"
            )
            notificationManager.notify(666, notificationError)
        }

        try {
            val response = apiService.getSensor(Utils.getISODate(), sourceSensor)

            if (response.isSuccessful) {
                val sensor = response.body()?.first()
                val notification = sensor?.let {
                    NotificationManager(applicationContext).showNotification(
                        getQualityAQI(sensor.quality.index, applicationContext),
                        "Sensor: ${sensor.description} - AQI: ${sensor.quality.index}",
                        it.source
                    )
                }

                sensor?.source?.let {
                    notificationManager.notify(it.hexToInt(), notification)
                    if(!isPeriodicNotify) cancelNotification()
                }
                Result.success()
            } else {
                cancelNotification()
                showErrorNotify()
                Result.failure()
            }
        } catch (e: Exception) {
            cancelNotification()
            showErrorNotify()
            Result.failure()
        }
    }
}