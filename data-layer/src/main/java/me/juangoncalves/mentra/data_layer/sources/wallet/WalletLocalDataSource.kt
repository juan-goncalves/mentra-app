package me.juangoncalves.mentra.data_layer.sources.wallet

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet


interface WalletLocalDataSource {

    suspend fun getAll(): List<Wallet>

    suspend fun save(wallet: Wallet)

    suspend fun delete(wallet: Wallet)

    suspend fun findByCoin(coin: Coin): List<Wallet>

    suspend fun findById(id: Long): Wallet?

    suspend fun update(wallet: Wallet, price: Price? = null)

    suspend fun updateValue(wallet: Wallet, price: Price)

    suspend fun getValueHistory(wallet: Wallet): List<Price>

    fun getWalletsStream(): Flow<List<Wallet>>

}