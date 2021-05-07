package me.juangoncalves.mentra.android_network.error

class CryptoCompareResponseException(message: String) : RuntimeException(message)

class ExchangeRatesApiException(val code: Int, message: String) : RuntimeException(message)