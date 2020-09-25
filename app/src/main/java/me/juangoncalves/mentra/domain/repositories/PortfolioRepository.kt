package me.juangoncalves.mentra.domain.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import java.time.LocalDate

interface PortfolioRepository {

    suspend fun getLatestPortfolioValue(): Either<Failure, Price?>

    suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit>

    suspend fun getPortfolioValueHistory(): Either<Failure, Map<LocalDate, Double>>

}