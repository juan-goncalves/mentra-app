package me.juangoncalves.mentra.domain_layer.errors

import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price


open class Failure

class PriceNotFound(val coin: Coin) : Failure()

class ServerFailure : Failure()

class StorageFailure : Failure()

class WalletCreationFailure : Failure()

class InternetConnectionFailure : Failure()

class FetchPriceFailure(val storedPrice: Price? = null) : Failure()

class NotFoundFailure : Failure()

class ExchangeRateNotAvailable : Failure()

class CurrenciesNotAvailable : Failure()