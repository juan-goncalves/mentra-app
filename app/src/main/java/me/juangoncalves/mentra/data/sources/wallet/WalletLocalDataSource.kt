package me.juangoncalves.mentra.data.sources.wallet

import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Wallet


interface WalletLocalDataSource {

    suspend fun getStoredWallets(): List<WalletModel>

    suspend fun storeWallet(wallet: Wallet)

    suspend fun findWalletsByCoin(coin: Coin): List<WalletModel>

}