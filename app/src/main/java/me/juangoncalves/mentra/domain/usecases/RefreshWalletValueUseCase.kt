package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Currency
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

class RefreshWalletValueUseCase @Inject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(
        wallet: Wallet,
        currency: Currency = Currency.USD
    ): Either<Failure, Price> {
        val coinPriceResult = coinRepository.getCoinPrice(wallet.coin, currency)
        val (_, coinPrice, timestamp) = coinPriceResult.rightValue ?: return coinPriceResult
        val walletValue = Price(currency, wallet.amount * coinPrice, timestamp)

        val updateResult = walletRepository.updateWalletValue(wallet, walletValue)
        updateResult.rightValue ?: return Either.Left(updateResult.requireLeft())

        return Either.Right(walletValue)
    }

}