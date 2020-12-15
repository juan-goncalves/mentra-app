package me.juangoncalves.mentra.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import me.juangoncalves.mentra.domain_layer.extensions.isLeft
import me.juangoncalves.mentra.domain_layer.usecases.portfolio.RefreshPortfolioValue


class PortfolioSnapshotWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val refreshPortfolioValue: RefreshPortfolioValue
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "portfolio_snapshot"
    }

    override suspend fun doWork(): Result {
        val result = refreshPortfolioValue()

        return when {
            result.isLeft() -> Result.retry()
            else -> Result.success()
        }
    }

}