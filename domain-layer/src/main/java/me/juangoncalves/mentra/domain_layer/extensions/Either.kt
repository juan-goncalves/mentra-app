package me.juangoncalves.mentra.domain_layer.extensions

import either.Either

typealias Right<T> = Either.Right<T>
typealias Left<T> = Either.Left<T>

val <L, R> Either<L, R>.rightValue: R?
    get() = (this as? Either.Right)?.value

val <L, R> Either<L, R>.leftValue: L?
    get() = (this as? Either.Left)?.value

fun <L, R> Either<L, R>.requireRight(): R = (this as Either.Right).value

fun <L, R> Either<L, R>.requireLeft(): L = (this as Either.Left).value

fun <L, R> Either<L, R>.isLeft(): Boolean = (this is Left)

fun <T> T.toRight(): Right<T> = Right(this)

fun <T> T.toLeft(): Left<T> = Left(this)

suspend inline fun <L, R> Either<L, R>.whenLeft(crossinline block: suspend (L) -> Unit) {
    if (isLeft()) {
        block(requireLeft())
    }
}