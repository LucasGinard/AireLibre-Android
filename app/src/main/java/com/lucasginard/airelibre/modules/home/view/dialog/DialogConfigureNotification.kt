package com.lucasginard.airelibre.modules.home.view.dialog

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lucasginard.airelibre.AireLibreApp
import com.lucasginard.airelibre.R
import com.lucasginard.airelibre.modules.about.ui.theme.AireLibreTheme
import com.lucasginard.airelibre.modules.home.model.Quality
import com.lucasginard.airelibre.modules.home.model.SensorResponse
import com.lucasginard.airelibre.modules.notifications.NotificationWorkManager
import com.lucasginard.airelibre.modules.notifications.model.NotificationSchedulePeriodicModel
import com.lucasginard.airelibre.utils.ComposablesUtils
import com.lucasginard.airelibre.utils.ThemeState
import com.lucasginard.airelibre.utils.nowDate
import java.util.Calendar

@Composable
fun DialogConfigureNotification(
    openDialog: MutableState<Boolean>,
    sensor: SensorResponse,
    callBackUpdate: () -> Unit
) {
    val font = ComposablesUtils.fonts
    val context = LocalContext.current
    val notificationWorkerManager = NotificationWorkManager(context)
    val mCalendar = Calendar.getInstance()

    var isBatteryOptimizationEnabled: () -> Boolean = {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    var goToSettingBattery: () -> Unit? = {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        context.startActivity(intent)
    }

    var selectedHour by remember { mutableStateOf(mCalendar[Calendar.HOUR_OF_DAY]) }
    var selectedMinute by remember {
        var minutes = mCalendar[Calendar.MINUTE]
        if (minutes >= 56) minutes += 3
        mutableStateOf(minutes)
    }
    var mTime by remember { mutableStateOf("$selectedHour:$selectedMinute") }

    // Calendar
    var selectedDateText by remember { mutableStateOf(nowDate()) }
    var selectedDay by remember { mutableStateOf(mCalendar[Calendar.DAY_OF_MONTH]) }
    var selectedMonth by remember { mutableStateOf(0) }

    // Weekly repeat, monthly repeat, and single notification options
    var repeatWeekly by remember { mutableStateOf(true) }

    // List of switches for each day of the week
    val selectedDays =
        remember { mutableStateListOf(false, false, false, false, false, false, false) }

    val mTimePickerDialog = TimePickerDialog(
        context,
        { _, mHour: Int, mMinute: Int ->
            selectedHour = mHour
            selectedMinute = mMinute
            mTime = "$selectedHour:$selectedMinute"
        }, mCalendar[Calendar.HOUR_OF_DAY], mCalendar[Calendar.MINUTE], true
    )

    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonths: Int, selectedDayOfMonth: Int ->
            selectedDateText = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
            selectedDay = selectedDayOfMonth
            selectedMonth = selectedMonths
        }, mCalendar[Calendar.YEAR], mCalendar[Calendar.MONTH], mCalendar[Calendar.DAY_OF_MONTH]
    )
    datePicker.datePicker.minDate = System.currentTimeMillis()

    val createNotificationDateAndTime = {
        if (repeatWeekly) {
            val daysOfWeek = mutableListOf<Int>()
            listOf(
                Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
            ).forEachIndexed { index, dayOfWeek ->
                if (selectedDays[index]) {
                    daysOfWeek.add(dayOfWeek)
                }
            }
            if (daysOfWeek.isNotEmpty()) {
                val scheduleModel = NotificationSchedulePeriodicModel(
                    daysSelected = daysOfWeek,
                    hour = selectedHour,
                    minute = selectedMinute,
                    sensor = sensor,
                    isPeriodic = repeatWeekly
                )

                notificationWorkerManager.schedulePeriodicWork(scheduleModel)
            }
        } else {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
            calendar.set(Calendar.MONTH, selectedMonth)

            val currentTime = System.currentTimeMillis()
            val scheduledTime = calendar.timeInMillis
            val delayMinutes = scheduledTime - currentTime

            notificationWorkerManager.scheduleOneTimeNotification(sensor,delayMinutes)
        }

        AireLibreApp.prefs.scheduleNotification(sensor.description)
        callBackUpdate()
    }

    Dialog(onDismissRequest = { openDialog.value = false }) {
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier.padding(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    text = context.getString(R.string.tvTitleConfigure)
                )

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sensors),
                        contentDescription = "iconSensor"
                    )
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

                    Text(
                        fontFamily = font,
                        fontWeight = FontWeight.Normal,
                        text = "Repetir:"
                    )

                    Row(
                        modifier = Modifier.padding(top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        TextButton(
                            onClick = { repeatWeekly = false},
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = if (!repeatWeekly) MaterialTheme.colors.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Una\nvez",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(3.dp))

                        TextButton(
                            onClick = { repeatWeekly = true },
                            colors = ButtonDefaults.textButtonColors(
                                backgroundColor = if (repeatWeekly) MaterialTheme.colors.primary else Color.Gray
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Cada\nSemana",
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    if (repeatWeekly) {
                        Text(
                            modifier = Modifier.padding(top = 15.dp),
                            fontFamily = font,
                            fontWeight = FontWeight.Normal,
                            text = context.getString(R.string.tvSelectDays)
                        )

                        Column(
                            modifier = Modifier.padding(top = 15.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    itemsIndexed(
                                        listOf(
                                            "L",
                                            "M",
                                            "Mi",
                                            "J",
                                            "V",
                                            "S",
                                            "D"
                                        )
                                    ) { index, day ->
                                        Button(
                                            onClick = {
                                                selectedDays[index] = !selectedDays[index]
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                backgroundColor = if (selectedDays[index]) MaterialTheme.colors.primary else Color.Gray
                                            ),
                                            shape = RoundedCornerShape(60),
                                            modifier = Modifier
                                                .height(40.dp)
                                                .padding(2.dp)
                                        ) {
                                            Text(text = day, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        Row(
                            modifier = Modifier.padding(top = 15.dp),
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
                    }

                    Row(
                        modifier = Modifier.padding(top = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            modifier = Modifier.width(80.dp),
                            fontFamily = font,
                            fontWeight = FontWeight.Normal,
                            text = "Hora:"
                        )
                        TextButton(onClick = { mTimePickerDialog.show() }) {
                            Text(text = mTime)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 4.dp),
                            onClick = {
                                openDialog.value = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.btnCancel))
                        }

                        Button(
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                createNotificationDateAndTime()
                                openDialog.value = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.btnConfirm))
                        }
                    }
                }
            }
        }
    }

}


@Preview
@Composable
fun previewDialogNotification() {
    AireLibreTheme {
        DialogConfigureNotification(openDialog = remember { mutableStateOf(true) },
            sensor = SensorResponse(
                description = "Luque A1",
                source = "213",
                longitude = 0.0,
                latitude = 0.0,
                quality = Quality("", 0),
                sensor = ""
            ), {})
    }
}