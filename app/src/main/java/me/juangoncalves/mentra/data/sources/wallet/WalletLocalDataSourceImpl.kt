package me.juangoncalves.mentra.data.sources.wallet

import me.juangoncalves.mentra.data.mapper.WalletMapper
import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet
import javax.inject.Inject

class WalletLocalDataSourceImpl @Inject constructor(
    private val walletDao: WalletDao,
    private val walletMapper: WalletMapper
) : WalletLocalDataSource {

    override suspend fun getStoredWallets(): List<WalletModel> {
        return orStorageException { walletDao.getAll() }
    }

    override suspend fun storeWallet(wallet: Wallet) {
        val model = walletMapper.map(wallet)
        orStorageException("Exception when saving wallet.") {
            walletDao.insertAll(model)
        }
    }

    override suspend fun findWalletsByCoin(coin: Coin): List<WalletModel> {
        return orStorageException { walletDao.findByCoin(coin.symbol) }
    }

    @Throws(StorageException::class)
    private suspend fun <T> orStorageException(
        message: String = "",
        execute: suspend () -> T
    ): T {
        return try {
            execute()
        } catch (e: Exception) {
            throw StorageException("$message\n$e")
        }
    }

}