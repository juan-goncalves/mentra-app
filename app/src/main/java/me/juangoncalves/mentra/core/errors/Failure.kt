package me.juangoncalves.mentra.core.errors

import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin

abstract class Failure

class PriceNotFound(val coins: List<Coin>) : Failure()