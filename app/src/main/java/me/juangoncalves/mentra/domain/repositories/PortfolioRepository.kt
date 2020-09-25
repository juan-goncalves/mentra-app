package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price

interface PortfolioRepository {

    suspend fun getLatestPortfolioValue(): Either<Failure, Price?>

    suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit>

}