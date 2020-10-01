package me.juangoncalves.mentra.data.sources.wallet

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.db.daos.WalletDao
import me.juangoncalves.mentra.db.daos.WalletValueDao
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.db.models.WalletValueModel
import me.juangoncalves.mentra.domain.errors.StorageException
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import javax.inject.Inject

class WalletLocalDataSourceImpl @Inject constructor(
    private val walletDao: WalletDao,
    private val walletValueDao: WalletValueDao
) : WalletLocalDataSource {

    override fun getWalletsStream(): Flow<List<WalletModel>> = walletDao.getWalletsStream()

    override suspend fun getAll(): List<WalletModel> {
        return orStorageException { walletDao.getAll() }
    }

    override suspend fun save(wallet: WalletModel) {
        orStorageException("Exception when saving wallet.") {
            walletDao.insertAll(wallet)
        }
    }

    override suspend fun findByCoin(coin: Coin): List<WalletModel> {
        return orStorageException { walletDao.findByCoin(coin.symbol) }
    }

    override suspend fun update(wallet: WalletModel, price: Price?) = orStorageException {
        walletDao.update(wallet)
        if (price != null) {
            val valueModel = WalletValueModel(wallet.id, price.value, price.date.toLocalDate())
            walletValueDao.insert(valueModel)
        }
    }

    override suspend fun updateValue(wallet: Wallet, price: Price) {
        val model = WalletValueModel(wallet.id, price.value, price.date.toLocalDate())
        orStorageException("Exception when inserting wallet value.") {
            walletValueDao.insert(model)
        }
    }

    override suspend fun getValueHistory(wallet: Wallet): List<WalletValueModel> {
        return orStorageException("Exception while fetching the wallet value history.") {
            walletValueDao.getWalletValueHistory(wallet.id)
        }
    }

    override suspend fun delete(wallet: WalletModel) {
        walletDao.delete(wallet)
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