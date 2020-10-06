package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.data.mapper.PortfolioValueMapper
import me.juangoncalves.mentra.db.daos.PortfolioDao
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioDao,
    private val portfolioValueMapper: PortfolioValueMapper,
    private val logger: Logger
) : PortfolioRepository {

    override val portfolioValue: Flow<Price> get() = _portfolioValue
    override val portfolioValueHistory: Flow<List<Price>> get() = _portfolioValueHistory

    private val _portfolioValue: Flow<Price> =
        portfolioDao.getPortfolioValueStream()
            .catch { logger.error(TAG, "Exception getting portfolio value.\n$it") }
            .filterNotNull()
            .map(portfolioValueMapper::map)

    private val _portfolioValueHistory: Flow<List<Price>> =
        portfolioDao.getPortfolioHistoricValuesStream()
            .catch { logger.error(TAG, "Exception getting portfolio value history.\n$it") }
            .map(portfolioValueMapper::map)

    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> =
        handleException {
            val model = portfolioValueMapper.map(value)
            portfolioDao.insertValue(model)
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