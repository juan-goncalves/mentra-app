package me.juangoncalves.mentra.core.errors

import me.juangoncalves.mentra.features.wallet_management.domain.entities.Price


class ServerException : RuntimeException()

class NotFoundException : RuntimeException()

class StorageException : RuntimeException()

class PriceCacheMissException(val lastestAvailablePrice: Price? = null) : RuntimeException()