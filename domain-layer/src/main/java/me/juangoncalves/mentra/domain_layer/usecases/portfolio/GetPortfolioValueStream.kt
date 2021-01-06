package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class GetPortfolioValueStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) : FlowUseCase<Price> {

    @ExperimentalCoroutinesApi
    override operator fun invoke(): Flow<Price> = portfolioRepository.portfolioValue.map { value ->
        val defaultValue = Price(
            BigDecimal.ZERO,
            Currency.getInstance("USD"),
            LocalDateTime.now()
        )
        value ?: defaultValue
    }

}