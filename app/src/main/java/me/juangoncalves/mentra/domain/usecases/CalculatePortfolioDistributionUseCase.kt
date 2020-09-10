package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.Right
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

class CalculatePortfolioDistributionUseCase @Inject constructor(
    private val getWallets: GetWalletsUseCase,
    private val refreshWalletValue: RefreshWalletValueUseCase
) {

    suspend operator fun invoke(): Either<Failure, Map<Coin, Double>> {
        val getWalletsResult = getWallets()
        val wallets = getWalletsResult.rightValue ?: return Left(getWalletsResult.requireLeft())

        val amountPerCoin = hashMapOf<Coin, Double>()
        wallets.forEach { wallet ->
            val walletValueResult = refreshWalletValue(wallet)
            val value = walletValueResult.rightValue ?: return Left(walletValueResult.requireLeft())
            amountPerCoin[wallet.coin] = amountPerCoin.getOrDefault(wallet.coin, 0.0) + value.value
        }

        val total = amountPerCoin.values.sum()
        val result = amountPerCoin.mapValues { (_, value) -> value / total }

        return Right(result)
    }
}