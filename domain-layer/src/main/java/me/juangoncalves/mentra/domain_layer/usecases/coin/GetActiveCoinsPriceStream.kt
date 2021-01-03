package me.juangoncalves.mentra.domain_layer.usecases.coin

import kotlinx.coroutines.flow.Flow
import me.juangoncalves.mentra.domain_layer.models.Coin
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.usecases.FlowUseCase
import javax.inject.Inject

class GetActiveCoinsPriceStream @Inject constructor(
    private val coinRepository: CoinRepository
) : FlowUseCase<Map<Coin, Price>> {

    override operator fun invoke(): Flow<Map<Coin, Price>> = coinRepository.pricesOfCoinsInUse

}