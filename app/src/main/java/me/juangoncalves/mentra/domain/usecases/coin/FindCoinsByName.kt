package me.juangoncalves.mentra.domain.usecases.coin

import either.Either
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.di.DefaultDispatcher
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import me.juangoncalves.mentra.extensions.toLeft
import me.juangoncalves.mentra.extensions.toRight
import java.util.*
import javax.inject.Inject

class FindCoinsByName @Inject constructor(
    private val coinRepository: CoinRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher
) : UseCase<String, List<Coin>> {

    private var allCoins: List<Coin>? = null

    override suspend fun invoke(params: String): Either<Failure, List<Coin>> =
        withContext(defaultDispatcher) {
            val query = params.trim().toLowerCase(Locale.ROOT)

            val coins = when (val safeCoins = allCoins) {
                null -> {
                    val result = coinRepository.getCoins()
                    result.rightValue ?: return@withContext result.requireLeft().toLeft()
                }
                else -> safeCoins
            }

            if (query.length in 0..2) {
                return@withContext coins.toRight()
            }

            coins.filter { coin -> coin.name.toLowerCase(Locale.ROOT).contains(query) }
                .sortedBy { match -> match.name.length - params.length }
                .toRight()
        }

}