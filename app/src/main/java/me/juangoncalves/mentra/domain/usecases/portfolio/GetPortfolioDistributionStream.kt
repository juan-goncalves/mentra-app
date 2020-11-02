package me.juangoncalves.mentra.domain.usecases.portfolio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.closeTo
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

class GetPortfolioDistributionStream @Inject constructor(
    private val portfolioRepository: PortfolioRepository,
    private val walletRepository: WalletRepository
) {

    operator fun invoke(): Flow<Map<Coin, Double>> = portfolioRepository.portfolioValue.map {
        val wallets = walletRepository.getWallets().rightValue ?: emptyList()

        val valuePerCoin = hashMapOf<Coin, Double>()
        wallets.forEach { wallet ->
            val price = walletRepository.getWalletValueHistory(wallet).rightValue?.firstOrNull()
            val value = price?.value ?: 0.0
            valuePerCoin[wallet.coin] = valuePerCoin.getOrDefault(wallet.coin, 0.0) + value
        }

        val total = valuePerCoin.values.sum()

        when {
            total closeTo 0.0 -> emptyMap()
            else -> valuePerCoin.mapValues { (_, value) -> value / total }
        }
    }

}