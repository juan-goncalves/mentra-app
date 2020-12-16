package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.models.WalletModel
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Wallet
import javax.inject.Inject

class WalletMapper @Inject constructor(
    private val coinLocalDataSource: CoinLocalDataSource
) {

    fun map(wallet: Wallet): WalletModel = WalletModel(wallet.coin.symbol, wallet.amount, wallet.id)

    suspend fun map(model: WalletModel): Wallet {
        val coin = coinLocalDataSource.findCoinBySymbol(model.coinSymbol)
            ?: throw IllegalArgumentException("Invalid wallet (coin ${model.coinSymbol} not found)")

        return Wallet(coin, model.amount, model.id)
    }

    suspend fun map(models: List<WalletModel>): List<Wallet> = models.map { map(it) }

}