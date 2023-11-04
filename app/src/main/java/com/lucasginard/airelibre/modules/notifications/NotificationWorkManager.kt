package com.lucasginard.airelibre.modules.notifications

import android.content.Context
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
                ), TimeUnit.MINUTES
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
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .addTag(sensor.description)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun calculateInitialDelay(selectedDays: List<Int>, hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK)

        val nextExecutionTime = Calendar.getInstance()
        nextExecutionTime.set(Calendar.HOUR_OF_DAY, hour)
        nextExecutionTime.set(Calendar.MINUTE, minute)
        nextExecutionTime.set(Calendar.SECOND, 0)
        nextExecutionTime.set(Calendar.MILLISECOND, 0)

        for (selectedDay in selectedDays) {
            if (selectedDay == currentDay && nextExecutionTime.timeInMillis <= calendar.timeInMillis) {
                nextExecutionTime.add(Calendar.DAY_OF_MONTH, 7)
            } else if (selectedDay > currentDay || (selectedDay == currentDay && nextExecutionTime.timeInMillis > calendar.timeInMillis)) {
                nextExecutionTime.set(Calendar.DAY_OF_WEEK, selectedDay)
                break
            }
        }

        val currentTime = System.currentTimeMillis()
        val scheduledTime = nextExecutionTime.timeInMillis

        return scheduledTime - currentTime
    }
    fun cancelPeriodicWork(nameWork: String) {
        WorkManager.getInstance(context).cancelUniqueWork(nameWork)
    }

    fun cancelOneTimeNotification(nameWork: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(nameWork)
    }

}