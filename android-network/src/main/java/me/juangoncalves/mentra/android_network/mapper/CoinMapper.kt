package me.juangoncalves.mentra.android_network.mapper

import me.juangoncalves.mentra.android_network.services.crypto_compare.models.CoinSchema
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.IconType
import javax.inject.Inject

class CoinMapper @Inject constructor() {

    fun map(schema: CoinSchema): Coin = with(schema) {
        if (name.isEmpty() || symbol.isEmpty()) return Coin.Invalid

        val position = schema.sortPosition.toIntOrNull() ?: return Coin.Invalid

        return Coin(name, symbol, imageUrl, IconType.Unknown, position)
    }
}