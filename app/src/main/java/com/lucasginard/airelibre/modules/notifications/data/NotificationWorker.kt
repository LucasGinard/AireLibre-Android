package com.lucasginard.airelibre.modules.notifications.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lucasginard.airelibre.modules.notifications.NotificationManager
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.Utils
import com.lucasginard.airelibre.utils.getQualityAQI
import com.lucasginard.airelibre.utils.hexToInt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NotificationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val apiService = BaseCallService.serviceSensor().create(APINotification::class.java)
        val sourceSensor = inputData.getString(Constants.SOURCE_SENSOR) ?: ""

        return suspendCoroutine { continuation ->
            GlobalScope.launch {
                val response = apiService.getSensor(Utils.getISODate(),sourceSensor)
                if (response.isSuccessful){
                    val sensor = response.body()?.first()
                    val notification = sensor?.let {
                        NotificationManager(applicationContext).showNotification(
                            "${getQualityAQI(sensor.quality.index,applicationContext)}",
                            "Sensor: ${sensor.description} - AQI: ${sensor.quality.index}",
                            it.source
                        )
                    }

                    val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
                    sensor?.source?.let {
                        notificationManager.notify(it.hexToInt(), notification)
                    }

                    continuation.resume(Result.success())
                }else{
                    continuation.resume(Result.failure())
                }
            }
        }
    }
}