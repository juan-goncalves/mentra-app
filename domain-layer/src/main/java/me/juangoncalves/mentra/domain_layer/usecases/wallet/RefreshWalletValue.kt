package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.CoinRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.Interactor
import javax.inject.Inject

class RefreshWalletValue @Inject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) : Interactor<Wallet, Price> {

    /**
     * Calculates and stores the [params] wallet value in USD using the most recent [Coin]
     * price available.
     */
    override suspend operator fun invoke(params: Wallet): Either<Failure, Price> {
        val coinPriceResult = coinRepository.getCoinPrice(params.coin)
        val (coinPrice, currency, timestamp) = coinPriceResult.rightValue ?: return coinPriceResult
        val walletValue = Price(coinPrice * params.amount, currency, timestamp)

        val updateResult = walletRepository.updateWallet(params, walletValue)
        // TODO: Use the failure from updateResult after its refactored
        updateResult.rightValue ?: return Either.Left(Failure.Unknown)

        return Either.Right(walletValue)
    }

}