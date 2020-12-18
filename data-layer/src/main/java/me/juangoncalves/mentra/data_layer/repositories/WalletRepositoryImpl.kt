package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.*
import me.juangoncalves.mentra.domain_layer.extensions.Right
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import java.util.*
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val localDataSource: WalletLocalDataSource,
    private val errorHandler: ErrorHandler
) : WalletRepository {

    override val wallets: Flow<List<Wallet>>
        get() = _wallets

    private val _wallets: Flow<List<Wallet>> = localDataSource.getWalletsStream()


    override suspend fun getWallets(): Either<Failure, List<Wallet>> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.getAll()
        }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.save(wallet)
        }

    override suspend fun deleteWallet(wallet: Wallet): Either<OldFailure, Unit> =
        withContext(Dispatchers.IO) {
            handleException {
                localDataSource.delete(wallet)
                Either.Right(Unit)
            }
        }

    override suspend fun findWalletsByCoin(coin: Coin): Either<OldFailure, List<Wallet>> =
        withContext(Dispatchers.IO) {
            handleException {
                val models = localDataSource.findByCoin(coin)
                Either.Right(models)
            }
        }

    override suspend fun findWalletById(id: Long): Either<OldFailure, Wallet?> =
        withContext(Dispatchers.IO) {
            handleException {
                val model = localDataSource.findById(id) ?: return@handleException Right(null)
                Right(model)
            }
        }


    override suspend fun updateWallet(wallet: Wallet, price: Price?): Either<OldFailure, Unit> =
        withContext(Dispatchers.IO) {
            handleException {
                localDataSource.update(wallet, price)
                Either.Right(Unit)
            }
        }

    override suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<OldFailure, Unit> =
        withContext(Dispatchers.IO) {
            handleException {
                localDataSource.updateValue(wallet, price)
                Either.Right(Unit)
            }
        }

    override suspend fun getWalletValueHistory(wallet: Wallet): Either<OldFailure, List<Price>> =
        withContext(Dispatchers.IO) {
            handleException {
                val valueModels = localDataSource.getValueHistory(wallet)
                val prices = withContext(Dispatchers.Default) {
                    valueModels.map { valueModel ->
                        Price(valueModel.value, Currency.getInstance("USD"), valueModel.timestamp)
                    }
                }
                Right(prices)
            }
        }

    private suspend fun <R> handleException(source: suspend () -> Right<R>): Either<OldFailure, R> {
        return try {
            source.invoke()
        } catch (e: Exception) {
            Either.Left(StorageFailure())
        }
    }

}