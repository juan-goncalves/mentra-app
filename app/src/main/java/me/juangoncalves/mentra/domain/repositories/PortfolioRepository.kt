package me.juangoncalves.mentra.domain.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price

interface PortfolioRepository {

    val portfolioValue: Flow<Price>

    val portfolioValueHistory: Flow<List<Price>>

    val portfolioDistribution: Flow<Map<Coin, Double>>

    suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit>

}