package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import java.time.LocalDate
import javax.inject.Inject

class GetPortfolioValueHistoryUseCase @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) {

    suspend operator fun invoke(): Either<Failure, Map<LocalDate, Double>> {
        return portfolioRepository.getPortfolioValueHistory()
    }

}