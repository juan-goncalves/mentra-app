package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.data.sources.portfolio.PortfolioLocalDataSource
import me.juangoncalves.mentra.db.models.PortfolioValueModel
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.toPrice
import me.juangoncalves.mentra.log.Logger
import java.time.LocalDate
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val localDataSource: PortfolioLocalDataSource,
    private val logger: Logger
) : PortfolioRepository {

    override suspend fun getLatestPortfolioValue(): Either<Failure, Price?> = handleException {
        val valueModel = localDataSource.getLatestValue()
        valueModel?.run { valueInUSD.toPrice(timestamp = date.atStartOfDay()) }
    }

    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> =
        handleException {
            val model = PortfolioValueModel(value.value, value.date.toLocalDate())
            localDataSource.saveValue(model)
        }

    override suspend fun getPortfolioValueHistory(): Either<Failure, Map<LocalDate, Double>> =
        handleException {
            localDataSource.getValueHistory()
                .sortedByDescending { it.date }
                .associateBy(
                    keySelector = { it.date },
                    valueTransform = { it.valueInUSD }
                )
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