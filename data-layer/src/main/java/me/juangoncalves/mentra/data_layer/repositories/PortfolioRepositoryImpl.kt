package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.StorageFailure
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.extensions.TAG
import me.juangoncalves.mentra.domain_layer.log.MentraLogger
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioLocalDataSource,
    private val logger: MentraLogger
) : PortfolioRepository {

    override val portfolioValue: Flow<Price> get() = _portfolioValue
    override val portfolioDailyValueHistory: Flow<List<Price>> get() = _portfolioValueHistory

    private val _portfolioValue: Flow<Price> =
        portfolioDao.getPortfolioValueStream()
            .catch { logger.error(TAG, "Exception getting portfolio value.\n$it") }
            .filterNotNull()

    private val _portfolioValueHistory: Flow<List<Price>> =
        portfolioDao.getPortfolioHistoricValuesStream()
            .catch { logger.error(TAG, "Exception getting portfolio value history.\n$it") }

    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> =
        withContext(Dispatchers.IO) {
            handleException {
                portfolioDao.insertValue(value)
            }
        }

    private suspend fun <R> handleException(source: suspend () -> R): Either<Failure, R> {
        return try {
            Right(source.invoke())
        } catch (e: Exception) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

}