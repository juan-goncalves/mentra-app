package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor() : PortfolioRepository {

    override suspend fun getLatestPortfolioValue(): Either<Failure, Price?> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> {
        TODO("Not yet implemented")
    }

}