package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Coin
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

class RefreshWalletValue @Inject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) : UseCase<Wallet, Price> {

    /**
     * Calculates and stores the [params] wallet value in USD using the most recent [Coin]
     * price available.
     */
    override suspend operator fun invoke(params: Wallet): Either<Failure, Price> {
        val coinPriceResult = coinRepository.getCoinPrice(params.coin)
        val (coinPrice, currency, timestamp) = coinPriceResult.rightValue ?: return coinPriceResult
        val walletValue = Price(coinPrice * params.amount, currency, timestamp)

        val updateResult = walletRepository.updateWalletValue(params, walletValue)
        updateResult.rightValue ?: return Either.Left(updateResult.requireLeft())

        return Either.Right(walletValue)
    }

}