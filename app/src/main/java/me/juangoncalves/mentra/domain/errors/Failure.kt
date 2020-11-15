package me.juangoncalves.mentra.domain.errors

import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price

open class Failure

class PriceNotFound(val coin: Coin) : Failure()

class ServerFailure : Failure()

class StorageFailure : Failure()

class WalletCreationFailure : Failure()

class InternetConnectionFailure : Failure()

class FetchPriceFailure(val storedPrice: Price? = null) : Failure()

class NotFoundFailure : Failure()