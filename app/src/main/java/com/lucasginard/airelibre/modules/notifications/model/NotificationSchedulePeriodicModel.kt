package com.lucasginard.airelibre.modules.notifications.model

import com.lucasginard.airelibre.modules.home.model.SensorResponse

data class NotificationSchedulePeriodicModel(
    val daysSelected: List<Int>,
    val hour: Int,
    val minute: Int,
    val sensor: SensorResponse,
    val isPeriodic:Boolean
)
