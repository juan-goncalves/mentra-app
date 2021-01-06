package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import either.Either
import either.fold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.*
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidUseCase
import me.juangoncalves.mentra.domain_layer.usecases.wallet.RefreshWalletValue
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

class RefreshPortfolioValue @Inject constructor(
    private val walletRepository: WalletRepository,
    private val portfolioRepository: PortfolioRepository,
    private val refreshWalletValue: RefreshWalletValue
) : VoidUseCase<Price> {

    override suspend operator fun invoke(): Either<Failure, Price> =
        withContext(Dispatchers.Default) {
            val getWalletsOp = walletRepository.getWallets()
            val wallets = getWalletsOp.rightValue
                ?: return@withContext getWalletsOp.requireLeft().toLeft()

            val currentValue = portfolioRepository.portfolioValue.firstOrNull()
            if (wallets.isEmpty() && currentValue == null) {
                val defaultValue = Price(
                    BigDecimal.ZERO,
                    Currency.getInstance("USD"),
                    LocalDateTime.now()
                )
                return@withContext defaultValue.toRight()
            }

            val total = wallets.map { async { refreshWalletValue(it) } }
                .awaitAll()
                .sumByBigDecimal { result ->
                    if (result.isLeft()) return@withContext result.requireLeft().toLeft()
                    result.requireRight().value
                }

            val totalPrice = total.toPrice(currency = Currency.getInstance("USD"))

            portfolioRepository.updatePortfolioUsdValue(total).fold(
                left = { failure -> failure.toLeft() },
                right = { totalPrice.toRight() }
            )
        }

    private inline fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> BigDecimal): BigDecimal {
        var sum = BigDecimal.ZERO
        for (element in this) {
            sum += selector(element)
        }
        return sum
    }

}