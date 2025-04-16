package lab06

import android.content.Context

class PreferencesManager(context: Context) {
    private val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_HOURS_BEFORE = "hours_before"
        const val KEY_REPEAT_INTERVAL = "repeat_interval"
    }

    fun saveHoursBefore(hours: Int) {
        prefs.edit().putInt(KEY_HOURS_BEFORE, hours).apply()
    }

    fun saveRepeatInterval(hours: Int) {
        prefs.edit().putInt(KEY_REPEAT_INTERVAL, hours).apply()
    }

    fun getHoursBefore(): Int = prefs.getInt(KEY_HOURS_BEFORE, 24)
    fun getRepeatInterval(): Int = prefs.getInt(KEY_REPEAT_INTERVAL, 4)
}
