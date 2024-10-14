package net.miyataroid.miyatamagrimoire

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class GrimoireApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GrimoireApp)
            modules
        }
    }
}