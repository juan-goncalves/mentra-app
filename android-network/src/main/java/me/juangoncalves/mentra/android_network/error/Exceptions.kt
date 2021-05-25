package me.juangoncalves.mentra.android_network.error

class CryptoCompareResponseException(message: String) : RuntimeException(message)

class CurrencyLayerApiException(val code: Int, message: String) : RuntimeException(message)