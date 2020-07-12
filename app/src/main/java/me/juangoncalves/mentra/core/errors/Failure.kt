package me.juangoncalves.mentra.core.errors

import me.juangoncalves.mentra.features.portfolio.domain.entities.Coin
import me.juangoncalves.mentra.features.portfolio.domain.entities.Price

open class Failure

class PriceNotFound(val coin: Coin) : Failure()

class ServerFailure : Failure()

class StorageFailure : Failure()

class InternetConnectionFailure : Failure()

class FetchPriceError(val storedPrice: Price? = null) : Failure()