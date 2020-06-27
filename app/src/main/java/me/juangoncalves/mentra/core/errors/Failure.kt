package me.juangoncalves.mentra.core.errors

import me.juangoncalves.mentra.features.wallet_management.domain.entities.Coin
import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price

abstract class Failure

class PriceNotFound(val coin: Coin) : Failure()

class ServerFailure : Failure()

class StorageFailure : Failure()

class FetchPriceError(val storedPrice: Price? = null) : Failure()