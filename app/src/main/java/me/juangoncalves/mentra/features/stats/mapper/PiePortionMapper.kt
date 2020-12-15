package me.juangoncalves.mentra.features.stats.mapper

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.pie.PiePortion
import javax.inject.Inject

class PiePortionMapper @Inject constructor() {

    @Suppress("RedundantSuspendModifier")
    suspend fun map(portfolioParts: Map<Coin, Double>): Array<PiePortion> =
        withContext(Dispatchers.Default) {
            portfolioParts.entries
                .map { (coin, percentage) -> PiePortion(percentage, coin.symbol) }
                .toTypedArray()
        }

}