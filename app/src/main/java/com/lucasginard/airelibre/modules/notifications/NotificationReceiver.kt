package com.lucasginard.airelibre.modules.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.notifications.data.NotificationWorker
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.hexToInt

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stringSensor = intent.getStringExtra(Constants.OBJECT_SENSOR)
        val sensor = Gson().fromJson(stringSensor,SensorResponse::class.java)

        val isShowPeriodic= intent.getBooleanExtra(Constants.NOTIFICATION_SENSOR_IS_PERIODIC,false)
        validateTypeNotificacionForRemove(isShowPeriodic,"${sensor.source.hexToInt()}")

        val notificationWorkRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
            .setInputData(Data.Builder().putString(Constants.SOURCE_SENSOR, sensor.source).build())
            .build()
        WorkManager.getInstance(context).enqueue(notificationWorkRequest)
    }

    private fun validateTypeNotificacionForRemove(isShowPeriodic: Boolean,sourceID:String){
        if (!isShowPeriodic){
            AireLibreApp.prefs.cancelScheduledNotification(sourceID)
        }
    }
}