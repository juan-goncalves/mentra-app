package me.juangoncalves.mentra.core.network.schemas

import me.juangoncalves.mentra.core.network.CryptoCompareResponse

typealias CoinListSchema = CryptoCompareResponse<Map<String, CoinSchema>>