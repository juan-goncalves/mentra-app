package me.juangoncalves.mentra.android_cache.sources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.android_cache.daos.WalletDao
import me.juangoncalves.mentra.android_cache.daos.WalletValueDao
import me.juangoncalves.mentra.android_cache.mappers.WalletMapper
import me.juangoncalves.mentra.android_cache.models.WalletValueModel
import me.juangoncalves.mentra.data_layer.sources.wallet.WalletLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import java.util.*
import javax.inject.Inject

class RoomWalletDataSource @Inject constructor(
    private val walletDao: WalletDao,
    private val walletValueDao: WalletValueDao,
    private val walletMapper: WalletMapper
) : WalletLocalDataSource {

    override fun getWalletsStream(): Flow<List<Wallet>> =
        walletDao.getWalletsStream().map(walletMapper::map)

    override suspend fun getAll(): List<Wallet> {
        return walletDao.getAll()
            .map { walletMapper.map(it) }
    }

    override suspend fun save(wallet: Wallet) {
        val model = walletMapper.map(wallet)
        walletDao.insertAll(model)
    }

    override suspend fun findByCoin(coin: Coin): List<Wallet> {
        return walletDao.findByCoin(coin.symbol)
            .map { walletMapper.map(it) }
    }

    override suspend fun findById(id: Long): Wallet? {
        return walletDao.findById(id)?.let { walletMapper.map(it) }
    }

    // TODO: Refactor to receive a BigDecimal instead of a price (to force / assume it is USD)
    override suspend fun update(wallet: Wallet, price: Price?) {
        val updates = walletMapper.map(wallet)
        val currentWallet = walletDao.findById(wallet.id) ?: return

        if (currentWallet != updates) walletDao.update(updates)

        if (price != null) {
            val valueModel = WalletValueModel(wallet.id, price.value, price.timestamp.toLocalDate())
            walletValueDao.insert(valueModel)
        }
    }

    // TODO: Remove method (use the regular update)
    override suspend fun updateValue(wallet: Wallet, price: Price) {
        val model = WalletValueModel(wallet.id, price.value, price.timestamp.toLocalDate())
        return walletValueDao.insert(model)
    }

    override suspend fun getValueHistory(wallet: Wallet): List<Price> {
        return walletValueDao.getWalletValueHistory(wallet.id)
            .map { valueModel ->
                Price(
                    valueModel.valueInUSD,
                    Currency.getInstance("USD"),
                    valueModel.date.atStartOfDay()
                )
            }
    }

    override suspend fun delete(wallet: Wallet) {
        val model = walletMapper.map(wallet)
        walletDao.delete(model)
    }

}