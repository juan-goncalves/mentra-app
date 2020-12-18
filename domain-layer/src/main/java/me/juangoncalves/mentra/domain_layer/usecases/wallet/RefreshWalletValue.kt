package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class RefreshWalletValue @Inject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) : UseCase<Wallet, Price> {

    /**
     * Calculates and stores the [params] wallet value in USD using the most recent [Coin]
     * price available.
     */
    override suspend operator fun invoke(params: Wallet): Either<OldFailure, Price> {
        val coinPriceResult = coinRepository.getCoinPrice(params.coin)
        val (coinPrice, currency, timestamp) = coinPriceResult.rightValue ?: return coinPriceResult
        val walletValue = Price(coinPrice * params.amount, currency, timestamp)

        val updateResult = walletRepository.updateWalletValue(params, walletValue)
        updateResult.rightValue ?: return Either.Left(updateResult.requireLeft())

        return Either.Right(walletValue)
    }

}