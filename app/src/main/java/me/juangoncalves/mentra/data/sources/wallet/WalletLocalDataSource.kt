package me.juangoncalves.mentra.data.sources.wallet

import me.juangoncalves.mentra.db.models.WalletModel


interface WalletLocalDataSource {

    suspend fun getStoredWallets(): List<WalletModel>

}