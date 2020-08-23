package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.errors.StorageFailure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.TAG
import me.juangoncalves.mentra.log.Logger

class WalletRepositoryImpl constructor(
    private val localDataSource: WalletLocalDataSource,
    private val walletMapper: WalletMapper,
    private val logger: Logger
) : WalletRepository {

    override suspend fun getWallets(): Either<Failure, List<Wallet>> {
        return try {
            val models = localDataSource.getStoredWallets()
            val wallets = models.map { walletMapper.map(it) }
            Either.Right(wallets)
        } catch (e: StorageException) {
            logger.error(TAG, "Error communicating with the local database.\n$$e")
            Either.Left(StorageFailure())
        }
    }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Nothing> {
        TODO("Not yet implemented")
    }

}