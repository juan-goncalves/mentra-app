package me.juangoncalves.mentra

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import me.juangoncalves.mentra.ui.common.WindowLayoutCallbacks
import me.juangoncalves.mentra.workers.PortfolioSnapshotWorker
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MentraApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        registerActivityLifecycleCallbacks(WindowLayoutCallbacks())
        schedulePortfolioSnapshots()
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()


    private fun schedulePortfolioSnapshots() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val repeatInterval = Duration.ofHours(12)

        val workRequest = PeriodicWorkRequestBuilder<PortfolioSnapshotWorker>(repeatInterval)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                PortfolioSnapshotWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
    }

}
