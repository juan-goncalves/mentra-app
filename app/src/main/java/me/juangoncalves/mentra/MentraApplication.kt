package me.juangoncalves.mentra

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.juangoncalves.mentra.core.presentation.WindowLayoutCallbacks

@HiltAndroidApp
class MentraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(WindowLayoutCallbacks())
    }

}
