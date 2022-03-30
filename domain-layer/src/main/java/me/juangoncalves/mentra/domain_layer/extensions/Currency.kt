package me.juangoncalves.mentra.domain_layer.extensions

import java.util.*

fun Currency?.orUSD(): Currency = this ?: Currency.getInstance("USD")
