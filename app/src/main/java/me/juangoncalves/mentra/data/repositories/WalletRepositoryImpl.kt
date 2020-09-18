package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.log.Logger
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val localDataSource: WalletLocalDataSource,
    private val walletMapper: WalletMapper,
    private val logger: Logger
) : WalletRepository {

    override suspend fun getWallets(): Either<Failure, List<Wallet>> {
        return try {
            val models = localDataSource.getAll()
            val wallets = models.map { walletMapper.map(it) }
            Either.Right(wallets)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Unit> {
        return try {
            val model = walletMapper.map(wallet)
            localDataSource.save(model)
            Either.Right(Unit)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

    override suspend fun findWalletsByCoin(coin: Coin): Either<Failure, List<Wallet>> {
        return try {
            val models = localDataSource.findByCoin(coin)
            val wallets = models.map { walletMapper.map(it) }
            Either.Right(wallets)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

    override suspend fun updateWalletValue(wallet: Wallet, price: Price): Either<Failure, Unit> {
        return try {
            localDataSource.updateValue(wallet, price)
            Either.Right(Unit)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

    override suspend fun getWalletValueHistory(wallet: Wallet): Either<Failure, List<Price>> {
        return try {
            val prices = localDataSource.getValueHistory(wallet).map { valueModel ->
                Price(Currency.USD, valueModel.valueInUSD, valueModel.date.atStartOfDay())
            }
            Right(prices)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Left(StorageFailure())
        }
    }

}