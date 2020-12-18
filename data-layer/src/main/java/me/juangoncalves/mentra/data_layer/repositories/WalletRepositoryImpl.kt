package me.juangoncalves.mentra.data_layer.repositories

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain_layer.errors.ErrorHandler
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.errors.runCatching
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
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

    override suspend fun deleteWallet(wallet: Wallet): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.delete(wallet)
        }

    override suspend fun findWalletsByCoin(coin: Coin): Either<Failure, List<Wallet>> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.findByCoin(coin)
        }

    override suspend fun findWalletById(id: Long): Either<Failure, Wallet?> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.findById(id)
        }

    override suspend fun updateWallet(wallet: Wallet, price: Price?): Either<Failure, Unit> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.update(wallet, price)
        }

    override suspend fun getWalletValueHistory(wallet: Wallet): Either<Failure, List<Price>> =
        errorHandler.runCatching(Dispatchers.IO) {
            localDataSource.getValueHistory(wallet)
        }

}