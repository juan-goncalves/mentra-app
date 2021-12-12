package me.juangoncalves.mentra

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import dagger.hilt.android.HiltAndroidApp
import either.fold
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.juangoncalves.mentra.domain_layer.usecases.preference.GetPeriodicRefreshPreference
import me.juangoncalves.mentra.workers.PortfolioSnapshotWorker
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MentraApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var getPeriodicRefreshPreference: GetPeriodicRefreshPreference

    override fun onCreate() {
        super.onCreate()
        schedulePortfolioSnapshots()
        configureCoil()
    }

    private fun configureCoil() {
        ImageLoader.Builder(this)
            .componentRegistry { add(SvgDecoder(this@MentraApplication)) }
            .build()
            .also { Coil.setImageLoader(it) }
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()


    private fun schedulePortfolioSnapshots() = GlobalScope.launch {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .build()

        val repeatInterval = getPeriodicRefreshPreference().fold(
            left = { Duration.ofHours(12) },
            right = { preferredDuration -> preferredDuration }
        )

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
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }

}
