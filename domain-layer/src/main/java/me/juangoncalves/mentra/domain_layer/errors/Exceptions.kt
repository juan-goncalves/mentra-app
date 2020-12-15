package me.juangoncalves.mentra.domain_layer.errors

import me.juangoncalves.mentra.domain_layer.models.Price


class ServerException(message: String = "") : RuntimeException(message)

class InternetConnectionException : RuntimeException()

class StorageException(message: String = "") : RuntimeException(message)

class PriceCacheMissException(val latestAvailablePrice: Price? = null) : RuntimeException()