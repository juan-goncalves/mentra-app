package me.juangoncalves.mentra.features.stats.mapper

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.pie.PiePortion
import javax.inject.Inject

class PiePortionMapper @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) {

    @Suppress("RedundantSuspendModifier")
    suspend fun map(portfolioParts: Map<Coin, Double>): Array<PiePortion> =
        withContext(defaultDispatcher) {
            portfolioParts.entries
                .map { (coin, percentage) -> PiePortion(percentage, coin.symbol) }
                .toTypedArray()
        }

}