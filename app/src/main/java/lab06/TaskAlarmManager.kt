package lab06

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import lab06.data.TodoTaskRepository
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class TaskAlarmManager(
    private val context: Context,
    private val todoTaskRepository: TodoTaskRepository,
    private val preferencesManager: PreferencesManager
) {

    suspend fun scheduleAlarmForClosestTask() {
        todoTaskRepository.getAllAsStream().collect { tasks ->
            val upcomingTask = tasks
                .filter { !it.isDone }
                .minByOrNull { it.deadline }

            if (upcomingTask != null) {
                val hoursBefore = preferencesManager.getHoursBefore()
                val repeatIntervalHours = preferencesManager.getRepeatInterval()

                val triggerTime = upcomingTask.deadline
                    .atStartOfDay()
                    .minusHours(hoursBefore.toLong())
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val repeatIntervalMillis = TimeUnit.HOURS.toMillis(repeatIntervalHours.toLong())

                cancelPreviousAlarm()
                scheduleRepeatingAlarm(triggerTime, repeatIntervalMillis)
            }
        }
    }

    private fun cancelPreviousAlarm() {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun scheduleRepeatingAlarm(triggerAtMillis: Long, repeatIntervalMillis: Long) {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Deadline")
        intent.putExtra(messageExtra, "Zbliża się termin zakończenia zadania")

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            repeatIntervalMillis,
            pendingIntent
        )
    }
}


