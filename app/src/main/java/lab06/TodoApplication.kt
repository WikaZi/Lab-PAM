package lab06

import android.app.Application
import lab06.data.AppContainer
import lab06.data.AppDataContainer

class TodoApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this.applicationContext)
    }
    companion object {
        lateinit var container: AppContainer
    }
}