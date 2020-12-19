package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioLocalDataSource: PortfolioLocalDataSource,
    private val errorHandler: ErrorHandler
) : PortfolioRepository {

    override val portfolioValue: Flow<Price> get() = _portfolioValue
    override val portfolioDailyValueHistory: Flow<List<Price>> get() = _portfolioValueHistory

    private val _portfolioValue: Flow<Price> by lazy {
        portfolioLocalDataSource.getPortfolioValueStream().filterNotNull()
    }

    private val _portfolioValueHistory: Flow<List<Price>> by lazy {
        portfolioLocalDataSource.getPortfolioHistoricValuesStream()
    }

    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            portfolioLocalDataSource.insertValue(value)
        }

}