package me.juangoncalves.mentra.data.sources.wallet

import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.errors.StorageException

class WalletLocalDataSourceImpl(
    private val walletDao: WalletDao
) : WalletLocalDataSource {

    override suspend fun getStoredWallets(): List<WalletModel> {
        return orStorageException { walletDao.getAll() }
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