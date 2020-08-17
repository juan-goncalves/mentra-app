package me.juangoncalves.mentra.data.repositories

import either.Either
import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.data.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository

class WalletRepositoryImpl constructor(
    private val localDataSource: WalletLocalDataSource,
    private val walletMapper: WalletMapper
) : WalletRepository {

    override suspend fun getWallets(): Either<Failure, List<Wallet>> {
        val models = localDataSource.getStoredWallets()
        val wallets = models.map { walletMapper.map(it) }
        return Either.Right(wallets)
    }

    override suspend fun createWallet(wallet: Wallet): Either<Failure, Nothing> {
        TODO("Not yet implemented")
    }

}