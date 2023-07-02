package com.lucasginard.airelibre.modules.home.view.dialog

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.gson.Gson
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.home.model.Quality
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.home.view.HomeFragment
import com.lucasginard.airelibre.modules.notifications.NotificationReceiver
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.Constants
import com.lucasginard.airelibre.utils.ThemeState
import com.lucasginard.airelibre.utils.hexToInt
import com.lucasginard.airelibre.utils.nowDate
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

@Composable
fun DialogConfigureNotification(openDialog: MutableState<Boolean>,sensor: SensorResponse) {
    val font = ComposablesUtils.fonts

    val context = LocalContext.current
    val mCalendar = Calendar.getInstance()
    var selectedHour by remember { mutableStateOf(mCalendar[Calendar.HOUR_OF_DAY]) }
    var selectedMinute by remember { mutableStateOf(mCalendar[Calendar.MINUTE]) }
    var mTime by remember { mutableStateOf("$selectedHour:$selectedMinute") }

    //calendar
    var selectedDateText by remember { mutableStateOf(nowDate()) }
    var selectedDay by remember { mutableStateOf(mCalendar[Calendar.DAY_OF_MONTH]) }
    var selectedMoth by remember { mutableStateOf(0) }

    var switchCheck by remember { mutableStateOf(false) }

    val mTimePickerDialog = TimePickerDialog(
        LocalContext.current,
        {_, mHour : Int, mMinute: Int ->
            selectedHour = mHour
            selectedMinute = mMinute
            mTime = "$selectedHour:$selectedMinute"
        }, mCalendar[Calendar.HOUR_OF_DAY], mCalendar[Calendar.MINUTE], false
    )

    val datePicker = DatePickerDialog(
        LocalContext.current,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            selectedDay = selectedDayOfMonth
            selectedMoth = selectedMonth
        }, mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], mCalendar[Calendar.DAY_OF_MONTH]
    )
    datePicker.datePicker.minDate = System.currentTimeMillis()

    val createNotificationDateAndTime = {
        val intent = Intent(context, NotificationReceiver::class.java)
        val sensorObject = Gson().toJson(sensor)
        intent.putExtra(Constants.OBJECT_SENSOR,sensorObject)
        intent.putExtra(Constants.NOTIFICATION_SENSOR_IS_PERIODIC,switchCheck)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sensor.source.hexToInt(),
            intent,
            PendingIntent.FLAG_MUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

        val interval: Long = when (calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            28 -> 1000L * 60L * 60L * 24L * 28L
            29 -> 1000L * 60L * 60L * 24L * 29L
            30 -> 1000L * 60L * 60L * 24L * 30L
            31 -> 1000L * 60L * 60L * 24L * 31L
            else -> throw IllegalArgumentException("Invalid number of days in month")
        }

        if (switchCheck){
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                interval,
                pendingIntent
            )
        }else{
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        AireLibreApp.prefs.scheduleNotification("${sensor.source.hexToInt()}")
    }

    Dialog(onDismissRequest = { openDialog.value = false }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                IconButton(
                    modifier = Modifier
                        .then(Modifier.size(30.dp))
                        .padding(bottom = 5.dp, top = 5.dp, end = 5.dp)
                        .align(Alignment.End),
                    onClick = {
                        openDialog.value = false
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = stringResource(id = R.string.contentOnBack),
                        tint = if (!ThemeState.isDark) Color.Black else Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    fontFamily = font,
                    fontWeight = FontWeight.Bold,
                    text = "Configure su notificación"
                )

                Row(
                    modifier = Modifier.padding(top= 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painter =painterResource(id = R.drawable.ic_sensors) , contentDescription = "iconSensor")
                    Text(
                        fontFamily = font,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        text = sensor.description
                    )
                }

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.width(80.dp),
                            fontFamily = font,
                            fontWeight = FontWeight.Normal,
                            text = "Fecha:"
                        )
                        TextButton(onClick = { datePicker.show() }) {
                            Text(text = selectedDateText)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.width(80.dp),
                            fontFamily = font,
                            fontWeight = FontWeight.Normal,
                            text = "Hora:"
                        )
                        TextButton(onClick = { mTimePickerDialog.show() }) {
                            Text(
                                text = mTime
                            )
                        }
                    }


                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.width(80.dp),
                            fontFamily = font,
                            fontWeight = FontWeight.Normal,
                            text = "Repetir todos los meses"
                        )
                        Switch(checked = switchCheck, onCheckedChange = {
                            switchCheck = !switchCheck
                        } )
                    }
                }

                Row(
                    modifier = Modifier
                    .padding(top = 15.dp)
                ){
                    OutlinedButton(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp),
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text(text = stringResource(id = R.string.btnCancel))
                    }

                    Button(
                        shape = RoundedCornerShape(8.dp),
                        onClick = {
                            createNotificationDateAndTime()
                            openDialog.value = false
                        }) {
                        Text(text = stringResource(id = R.string.btnConfirm))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun previewDialogNotification(){
    AireLibreTheme {
        DialogConfigureNotification(openDialog = remember { mutableStateOf(true) },
            sensor = SensorResponse(
            description = "Luque A1",
            source = "213",
            longitude = 0.0,
            latitude = 0.0,
            quality = Quality("",0),
            sensor = ""
        ))
    }
}