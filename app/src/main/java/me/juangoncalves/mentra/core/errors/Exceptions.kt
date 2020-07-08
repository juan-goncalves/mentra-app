package me.juangoncalves.mentra.core.errors

import me.juangoncalves.mentra.features.portfolio.domain.entities.Price


class ServerException(message: String = "") : RuntimeException(message)

class InternetConnectionException : RuntimeException()

class StorageException : RuntimeException()

class PriceCacheMissException(val latestAvailablePrice: Price? = null) : RuntimeException()