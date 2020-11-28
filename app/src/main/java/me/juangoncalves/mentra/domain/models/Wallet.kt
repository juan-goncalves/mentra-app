package me.juangoncalves.mentra.domain.models

import java.math.BigDecimal

data class Wallet(
    val coin: Coin,
    val amount: BigDecimal,
    val id: Long = 0
) {

    constructor(
        coin: Coin,
        amount: Double,
        id: Long = 0
    ) : this(coin, amount.toBigDecimal(), id)

}