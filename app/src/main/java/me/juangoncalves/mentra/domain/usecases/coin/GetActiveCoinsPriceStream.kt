package me.juangoncalves.mentra.domain.usecases.coin

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import javax.inject.Inject

class GetActiveCoinsPriceStream @Inject constructor(
    private val coinRepository: CoinRepository
) {

    @ExperimentalCoroutinesApi
    operator fun invoke(): Flow<Map<Coin, Price>> = coinRepository.pricesOfCoinsInUse

}