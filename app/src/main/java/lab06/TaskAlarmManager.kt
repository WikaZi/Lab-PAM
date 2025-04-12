package lab06

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import lab06.data.TodoTaskRepository
import java.time.ZoneOffset

class TaskAlarmManager(
    private val context: Context,
    private val todoTaskRepository: TodoTaskRepository
) {

     suspend fun scheduleAlarmForClosestTask() {
        todoTaskRepository.getAllAsStream().collect { tasks ->
            val upcomingTask = tasks
                .filter { !it.isDone }
                .minByOrNull { it.deadline }

            upcomingTask?.let { task ->
                val timeBeforeDeadline = task.deadline.minusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000
                scheduleAlarm(timeBeforeDeadline)
            }
        }
    }

    private fun scheduleAlarm(time: Long) {
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
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}

