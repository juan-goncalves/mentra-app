package me.juangoncalves.mentra.android_cache.mappers

import me.juangoncalves.mentra.android_cache.entities.WalletEntity
import me.juangoncalves.mentra.data_layer.sources.coin.CoinLocalDataSource
import me.juangoncalves.mentra.domain_layer.models.Wallet
import javax.inject.Inject

class WalletMapper @Inject constructor(
    private val coinLocalDataSource: CoinLocalDataSource
) {

    fun map(wallet: Wallet): WalletEntity = WalletEntity(wallet.coin.symbol, wallet.amount, wallet.id)

    suspend fun map(entity: WalletEntity): Wallet {
        val coin = coinLocalDataSource.findCoinBySymbol(entity.coinSymbol)
            ?: throw IllegalArgumentException("Invalid wallet (coin ${entity.coinSymbol} not found)")

        return Wallet(coin, entity.amount, entity.id)
    }

    suspend fun map(entities: List<WalletEntity>): List<Wallet> = entities.map { map(it) }

}