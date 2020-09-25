package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.data.mapper.PortfolioValueMapper
import me.juangoncalves.mentra.db.daos.PortfolioDao
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class PortfolioRepositoryImpl @Inject constructor(
    private val portfolioDao: PortfolioDao,
    private val portfolioValueMapper: PortfolioValueMapper,
    private val walletRepository: WalletRepository,
    private val logger: Logger
) : PortfolioRepository {

    override val portfolioValue: Flow<Price> get() = _portfolioValue
    override val portfolioValueHistory: Flow<List<Price>> get() = _portfolioValueHistory
    override val portfolioDistribution: Flow<Map<Coin, Double>> get() = _portfolioDistribution

    private val _portfolioValue: Flow<Price> =
        portfolioDao.getPortfolioValue().map(portfolioValueMapper::map)

    private val _portfolioValueHistory: Flow<List<Price>> =
        portfolioDao.getPortfolioValueHistory().map(portfolioValueMapper::map)

    private val _portfolioDistribution: Flow<Map<Coin, Double>> =
        _portfolioValue.onEachCalculateDistribution()


    override suspend fun updatePortfolioValue(value: Price): Either<Failure, Unit> =
        handleException {
            val model = portfolioValueMapper.map(value)
            portfolioDao.insertValue(model)
        }

    private fun <T> Flow<T>.onEachCalculateDistribution(): Flow<Map<Coin, Double>> = map {
        val wallets = walletRepository.getWallets().rightValue ?: emptyList()

        val amountPerCoin = hashMapOf<Coin, Double>()
        wallets.forEach { wallet ->
            val price = walletRepository.getWalletValueHistory(wallet).rightValue?.firstOrNull()
            val value = price?.value ?: 0.0
            amountPerCoin[wallet.coin] = amountPerCoin.getOrDefault(wallet.coin, 0.0) + value
        }

        val total = amountPerCoin.values.sum()
        amountPerCoin.mapValues { (_, value) -> value / total }
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