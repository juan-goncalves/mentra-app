package me.juangoncalves.mentra.data.repositories

import either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.di.IoDispatcher
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val localDataSource: WalletLocalDataSource,
    private val walletMapper: WalletMapper,
    private val logger: Logger
) : WalletRepository {

    override val wallets: Flow<List<Wallet>>
        get() = _wallets

    private val _wallets: Flow<List<Wallet>> =
        localDataSource.getWalletsStream().map(walletMapper::map)

    override suspend fun getWallets(): Either<Failure, List<Wallet>> = withContext(ioDispatcher) {
        handleException {
            val models = localDataSource.getAll()
            val wallets = models.map { walletMapper.map(it) }
            Either.Right(wallets)
        }
    }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Unit> =
        withContext(ioDispatcher) {
            handleException {
                val model = walletMapper.map(wallet)
                localDataSource.save(model)
                Either.Right(Unit)
            }
        }

    override suspend fun deleteWallet(wallet: Wallet): Either<Failure, Unit> =
        withContext(ioDispatcher) {
            handleException {
                val model = walletMapper.map(wallet)
                localDataSource.delete(model)
                Either.Right(Unit)
            }
        }

    override suspend fun findWalletsByCoin(coin: Coin): Either<Failure, List<Wallet>> =
        withContext(ioDispatcher) {
            handleException {
                val models = localDataSource.findByCoin(coin)
                val wallets = withContext(defaultDispatcher) {
                    models.map { walletMapper.map(it) }
                }
                Either.Right(wallets)
            }
        }

    override suspend fun updateWallet(wallet: Wallet, price: Price?): Either<Failure, Unit> =
        withContext(ioDispatcher) {
            handleException {
                val model = walletMapper.map(wallet)
                localDataSource.update(model, price)
                Either.Right(Unit)
            }
        }

    override suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<Failure, Unit> =
        withContext(ioDispatcher) {
            handleException {
                localDataSource.updateValue(wallet, price)
                Either.Right(Unit)
            }
        }

    override suspend fun getWalletValueHistory(wallet: Wallet): Either<Failure, List<Price>> =
        withContext(ioDispatcher) {
            handleException {
                val valueModels = localDataSource.getValueHistory(wallet)
                val prices = withContext(defaultDispatcher) {
                    valueModels.map { valueModel ->
                        Price(Currency.USD, valueModel.valueInUSD, valueModel.date.atStartOfDay())
                    }
                }
                Right(prices)
            }
        }

    private suspend fun <R> handleException(source: suspend () -> Right<R>): Either<Failure, R> {
        return try {
            source.invoke()
        } catch (e: Exception) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

}