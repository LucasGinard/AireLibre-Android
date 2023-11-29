package com.lucasginard.airelibre.modules.notifications

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.notifications.data.NotificationWorkService
import com.lucasginard.airelibre.modules.notifications.model.NotificationSchedulePeriodicModel
import com.lucasginard.airelibre.utils.Constants
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationWorkManager(private val context: Context) {

    fun schedulePeriodicWork(scheduleModel: NotificationSchedulePeriodicModel) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val repeatInterval = 7L

        val inputData = Data.Builder()
            .putString(Constants.SOURCE_SENSOR, scheduleModel.sensor.source)
            .putString(Constants.ID_SENSOR_NOTIFICATION, scheduleModel.sensor.description)
            .putBoolean(Constants.NOTIFICATION_SENSOR_IS_PERIODIC, scheduleModel.isPeriodic)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<NotificationWorkService>(
            repeatInterval,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(
                calculateInitialDelay(
                    scheduleModel.daysSelected,
                    scheduleModel.hour,
                    scheduleModel.minute
                ), TimeUnit.MILLISECONDS
            )
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            scheduleModel.sensor.description,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun scheduleOneTimeNotification(sensor: SensorResponse, delayMinutes: Long) {
        val inputData = Data.Builder()
            .putString(Constants.SOURCE_SENSOR, sensor.source)
            .putString(Constants.ID_SENSOR_NOTIFICATION, sensor.description)
            .putBoolean(Constants.NOTIFICATION_SENSOR_IS_PERIODIC, false)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<NotificationWorkService>()
            .setInputData(inputData)
            .setInitialDelay(delayMinutes, TimeUnit.MILLISECONDS)
            .addTag(sensor.description)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun calculateInitialDelay(selectedDays: List<Int>, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val nextDay = selectedDays.minByOrNull { day ->
            val daysUntil = (day - currentDay + 7) % 7
            if (daysUntil == 0 && hour * 60 + minute < calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)) {
                7
            } else {
                daysUntil
            }
        } ?: selectedDays.firstOrNull() ?: return -1

        val nextExecutionTime = Calendar.getInstance()
        nextExecutionTime.set(Calendar.HOUR_OF_DAY, hour)
        nextExecutionTime.set(Calendar.MINUTE, minute)
        nextExecutionTime.set(Calendar.SECOND, 0)
        nextExecutionTime.set(Calendar.MILLISECOND, 0)
        nextExecutionTime.add(Calendar.DAY_OF_MONTH, (nextDay - currentDay + 7) % 7)

        val currentTime = System.currentTimeMillis()
        val scheduledTime = nextExecutionTime.timeInMillis

        Log.d("NotificationWorkManager", "Selected days: $selectedDays")
        Log.d("NotificationWorkManager", "Current day: $currentDay")
        Log.d("NotificationWorkManager", "Next day: $nextDay")
        Log.d("NotificationWorkManager", "Scheduled time: ${nextExecutionTime.time}")
        Log.d("NotificationWorkManager", "Current time: ${calendar.time}")
        Log.d("NotificationWorkManager", "Initial delay: ${scheduledTime - currentTime} milliseconds")

        return scheduledTime - currentTime
    }
    fun cancelPeriodicWork(nameWork: String) {
        WorkManager.getInstance(context).cancelUniqueWork(nameWork)
    }

    fun cancelOneTimeNotification(nameWork: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(nameWork)
    }

}