package me.juangoncalves.mentra.domain.errors

import me.juangoncalves.mentra.domain.models.Price


class ServerException(message: String = "") : RuntimeException(message)

class InternetConnectionException : RuntimeException()

class StorageException(message: String = "") : RuntimeException(message)

class PriceCacheMissException(val latestAvailablePrice: Price? = null) : RuntimeException()