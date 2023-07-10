package com.lucasginard.airelibre.utils

import android.app.AlertDialog
import android.content.Context
import com.lucasginard.airelibre.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

object Utils {
    fun getISODate(): String {
        val calendar = Calendar.getInstance()
        var date = calendar.time
        date = Date(date.time - 60 * 60000)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val sdf2 = SimpleDateFormat("HH:mm dd-MM")
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        sdf2.timeZone = TimeZone.getTimeZone("GMT")
        SessionCache.lastUpdate = "GMT ${sdf2.format(date)}"
        return sdf.format(date)
    }

    fun showDialog(context: Context, message: String, callback: () -> Unit) {
        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(message)
            .setPositiveButton(context.getString(R.string.btnAccept)) { _, _ ->
                callback.invoke()
            }
            .setNegativeButton(context.getString(R.string.btnCancel), null)

        val dialog = dialogBuilder.create()
        dialog.show()
    }

}