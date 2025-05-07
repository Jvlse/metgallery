package de.example.met_gallery

import android.app.Application
import de.example.met_gallery.data.AppContainer
import de.example.met_gallery.data.DefaultAppContainer
import de.example.met_gallery.data.appModule
import org.koin.core.context.startKoin

class App : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(appModule)
            container = DefaultAppContainer()
        }
    }
}
