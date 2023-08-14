package com.lucasginard.airelibre.modules.notifications.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.notifications.NotificationManager
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.Utils
import com.lucasginard.airelibre.utils.getQualityAQI
import com.lucasginard.airelibre.utils.hexToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val apiService = BaseCallService.serviceSensor().create(APINotification::class.java)
        val sourceSensor = inputData.getString(Constants.SOURCE_SENSOR) ?: ""
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

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
                }
                Result.success()
            } else {
                notificationManager.cancel(sourceSensor.hexToInt())
                AireLibreApp.prefs.cancelScheduledNotification("${sourceSensor.hexToInt()}")
                Result.failure()
            }
        } catch (e: Exception) {
            notificationManager.cancel(sourceSensor.hexToInt())
            AireLibreApp.prefs.cancelScheduledNotification("${sourceSensor.hexToInt()}")
            Result.failure()
        }
    }
}