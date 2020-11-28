package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.CoinRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

// TODO: Implement `UseCase`
class RefreshWalletValue @Inject constructor(
    private val coinRepository: CoinRepository,
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(wallet: Wallet): Either<Failure, Price> {
        val coinPriceResult = coinRepository.getCoinPrice(wallet.coin)
        val (coinPrice, currency, timestamp) = coinPriceResult.rightValue ?: return coinPriceResult
        val walletValue = Price(coinPrice * wallet.amount, currency, timestamp)

        val updateResult = walletRepository.updateWalletValue(wallet, walletValue)
        updateResult.rightValue ?: return Either.Left(updateResult.requireLeft())

        return Either.Right(walletValue)
    }

}