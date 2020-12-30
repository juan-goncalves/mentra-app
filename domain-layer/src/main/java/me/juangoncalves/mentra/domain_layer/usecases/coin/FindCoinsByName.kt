package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.extensions.toLeft
import me.juangoncalves.mentra.domain_layer.extensions.toRight
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import java.util.*
import javax.inject.Inject

class FindCoinsByName @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<String, List<Coin>> {

    private var allCoins: List<Coin>? = null

    override suspend fun invoke(params: String): Either<Failure, List<Coin>> =
        withContext(Dispatchers.Default) {
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

            coins
                .filter { coin ->
                    ensureActive()
                    coin.name.toLowerCase(Locale.ROOT).contains(query)
                }
                .sortedBy { match ->
                    ensureActive()
                    match.name.length - params.length
                }
                .toRight()
        }

}
