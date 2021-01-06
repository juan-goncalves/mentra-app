package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Price
import java.math.BigDecimal

interface PortfolioRepository {

    /** Stream of the most recent portfolio value */
    val portfolioValue: Flow<Price?>

    /** Stream of the complete daily portfolio value history on ascending order */
    val portfolioDailyValueHistory: Flow<List<Price>>

    suspend fun updatePortfolioUsdValue(value: BigDecimal): Either<Failure, Unit>

}