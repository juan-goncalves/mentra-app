package me.juangoncalves.mentra

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import me.juangoncalves.mentra.ui.common.WindowLayoutCallbacks

@HiltAndroidApp
class MentraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(WindowLayoutCallbacks())
    }

}
