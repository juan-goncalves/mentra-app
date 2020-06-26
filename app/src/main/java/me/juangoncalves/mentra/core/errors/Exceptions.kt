package me.juangoncalves.mentra.core.errors


class ServerException : RuntimeException()

class NotFoundException : RuntimeException()

class StorageException : RuntimeException()

class CacheMissException : RuntimeException()