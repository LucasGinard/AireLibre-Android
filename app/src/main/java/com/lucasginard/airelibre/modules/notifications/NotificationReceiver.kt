package com.lucasginard.airelibre.modules.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.notifications.data.ServiceSensor
import com.lucasginard.airelibre.utils.Constants

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stringSensor = intent.getStringExtra(Constants.OBJECT_SENSOR)
        val sensor = Gson().fromJson(stringSensor,SensorResponse::class.java)

        val serviceIntent = Intent(context, ServiceSensor::class.java)
        serviceIntent.putExtra(Constants.SOURCE_SENSOR,sensor.source)
        context.startService(serviceIntent)
    }
}