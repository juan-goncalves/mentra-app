package me.juangoncalves.mentra.domain_layer.usecases.coin

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class FindCoinsByName @Inject constructor(
    private val coinRepository: CoinRepository
) : UseCase<String, List<Coin>> {

    private var allCoins: List<Coin>? = null

    override suspend fun invoke(params: String): Either<OldFailure, List<Coin>> = TODO()
//        withContext(Dispatchers.Default) {
//            val query = params.trim().toLowerCase(Locale.ROOT)
//
//            val coins = when (val safeCoins = allCoins) {
//                null -> {
//                    val result = coinRepository.getCoins()
//                    result.rightValue ?: return@withContext result.requireLeft().toLeft()
//                }
//                else -> safeCoins
//            }
//
//            if (query.length in 0..2) {
//                return@withContext coins.toRight()
//            }
//
//            coins
//                .filter { coin ->
//                    ensureActive()
//                    coin.name.toLowerCase(Locale.ROOT).contains(query)
//                }
//                .sortedBy { match ->
//                    ensureActive()
//                    match.name.length - params.length
//                }
//                .toRight()
//        }

}
