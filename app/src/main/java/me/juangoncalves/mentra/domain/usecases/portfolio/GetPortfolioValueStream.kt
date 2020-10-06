package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import javax.inject.Inject

class GetPortfolioValueStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) {

    operator fun invoke(): Flow<Price> = portfolioRepository.portfolioValue

}