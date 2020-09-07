package me.juangoncalves.mentra.extensions

import either.Either

val <L, R> Either<L, R>.rightValue: R?
    get() = (this as? Either.Right)?.value

val <L, R> Either<L, R>.leftValue: L?
    get() = (this as? Either.Left)?.value

fun <L, R> Either<L, R>.requireRight(): R = (this as Either.Right).value

fun <L, R> Either<L, R>.requireLeft(): L = (this as Either.Left).value