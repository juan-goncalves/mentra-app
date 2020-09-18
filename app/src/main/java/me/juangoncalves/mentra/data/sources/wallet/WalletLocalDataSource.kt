package me.juangoncalves.mentra.data.sources.wallet

import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.db.models.WalletValueModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet


interface WalletLocalDataSource {

    suspend fun getAll(): List<WalletModel>

    suspend fun save(wallet: Wallet)

    suspend fun delete(wallet: WalletModel)

    suspend fun findByCoin(coin: Coin): List<WalletModel>

    suspend fun updateValue(wallet: Wallet, price: Price)

    suspend fun getValueHistory(wallet: Wallet): List<WalletValueModel>

}