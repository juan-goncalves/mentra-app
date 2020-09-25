package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import javax.inject.Inject

class GetLatestPortfolioValue @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) {

    suspend operator fun invoke(): Either<Failure, Price?> =
        portfolioRepository.getLatestPortfolioValue()

}