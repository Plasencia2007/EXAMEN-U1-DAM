package pe.upeu.biblioandes

import android.app.Application
import org.koin.android.ext.koin.androidContext
import pe.upeu.biblioandes.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
        }
    }
}
