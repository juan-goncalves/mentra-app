package me.juangoncalves.mentra.data.sources.wallet

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.db.models.WalletValueModel
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet


interface WalletLocalDataSource {

    suspend fun getAll(): List<WalletModel>

    suspend fun save(wallet: WalletModel)

    suspend fun delete(wallet: WalletModel)

    suspend fun findByCoin(coin: Coin): List<WalletModel>

    suspend fun findById(id: Long): WalletModel?

    suspend fun update(wallet: WalletModel, price: Price? = null)

    suspend fun updateValue(wallet: Wallet, price: Price)

    suspend fun getValueHistory(wallet: Wallet): List<WalletValueModel>

    fun getWalletsStream(): Flow<List<WalletModel>>

}