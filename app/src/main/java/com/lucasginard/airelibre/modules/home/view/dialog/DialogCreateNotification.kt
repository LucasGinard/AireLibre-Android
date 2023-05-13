package com.lucasginard.airelibre.modules.home.view.dialog

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.modules.notifications.NotificationReceiver
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.hexToInt
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun DialogConfigureNotification(openDialog: MutableState<Boolean>, context: Context,sensor: SensorResponse) {
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]
    val mTime = remember { mutableStateOf("") }

    //calendar
    val year = mCalendar[Calendar.YEAR]
    val month = mCalendar[Calendar.MONTH]
    val dayOfMonth = mCalendar[Calendar.DAY_OF_MONTH]
    var selectedDateText by remember { mutableStateOf("") }

    val mTimePickerDialog = TimePickerDialog(
        context,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        }, mHour, mMinute, false
    )

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
        }, year, month, dayOfMonth
    )

    Dialog(onDismissRequest = { openDialog.value = false }) {
        Column {
            Button(onClick = { mTimePickerDialog.show() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF34BE82))) {
                Text(text = "Open Time Picker", color = Color.White)
            }

            Button(onClick = { datePicker.show() }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF34BE82))) {
                Text(text = "Open date Picker", color = Color.White)
            }

            Button(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Select")
            }
        }
    }
}

private fun createNotification(fragment: HomeFragment, sensor: SensorResponse) {
    val tiempoNotificacion = System.currentTimeMillis() + 5000

    val intent = Intent(fragment.requireContext(), NotificationReceiver::class.java)
    val sensorObject = Gson().toJson(sensor)
    intent.putExtra(Constants.OBJECT_SENSOR,sensorObject)

    val pendingIntent = PendingIntent.getBroadcast(
        fragment.requireContext(),
        sensor.source.hexToInt(),
        intent,
        PendingIntent.FLAG_MUTABLE
    )
    val alarmManager = fragment.activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoNotificacion, pendingIntent)
}