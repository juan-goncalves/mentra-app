package me.juangoncalves.mentra.domain_layer.errors

import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price


open class OldFailure

class PriceNotFound(val coin: Coin) : OldFailure()

class ServerFailure : OldFailure()

class StorageFailure : OldFailure()

class WalletCreationFailure : OldFailure()

class InternetConnectionFailure : OldFailure()

class FetchPriceFailure(val storedPrice: Price? = null) : OldFailure()

class NotFoundFailure : OldFailure()

class ExchangeRateNotAvailable : OldFailure()

class CurrenciesNotAvailable : OldFailure()