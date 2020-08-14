package me.juangoncalves.mentra.data.mapper

import me.juangoncalves.mentra.db.models.WalletModel
import me.juangoncalves.mentra.domain.models.Wallet
import javax.inject.Inject

class WalletMapper @Inject constructor() {

    fun map(wallet: Wallet): WalletModel = WalletModel(wallet.coin.symbol, wallet.amount, wallet.id)

}