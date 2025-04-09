package lab06

import android.app.Application
import lab06.data.AppContainer
import lab06.data.AppDataContainer

class TodoApplication: Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
}