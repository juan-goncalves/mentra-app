package me.juangoncalves.mentra.domain_layer.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Price

interface PortfolioRepository {

    /** Stream of the most recent portfolio value */
    val portfolioValue: Flow<Price>

    /** Stream of the complete daily portfolio value history on ascending order */
    val portfolioDailyValueHistory: Flow<List<Price>>

    suspend fun updatePortfolioValue(value: Price): Either<OldFailure, Unit>

}