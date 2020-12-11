package me.juangoncalves.mentra.domain.usecases.portfolio

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.VoidUseCase
import me.juangoncalves.mentra.domain.usecases.wallet.RefreshWalletValue
import me.juangoncalves.mentra.extensions.*
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class RefreshPortfolioValue @Inject constructor(
    private val walletRepository: WalletRepository,
    private val portfolioRepository: PortfolioRepository,
    private val refreshWalletValue: RefreshWalletValue
) : VoidUseCase<Price> {

    override suspend operator fun invoke(): Either<Failure, Price> {
        val getWalletsResult = walletRepository.getWallets()
        val wallets = getWalletsResult.rightValue ?: return Left(getWalletsResult.requireLeft())

        val (total, totalCalculationFailure) = withContext(Dispatchers.Default) {
            var total = BigDecimal.ZERO
            for (wallet in wallets) {
                val refreshResult = refreshWalletValue(wallet)
                if (refreshResult.isLeft()) {
                    return@withContext Price.None to refreshResult.requireLeft()
                }
                total += refreshResult.requireRight().value
            }

            val totalPrice = total.toPrice(currency = Currency.getInstance("USD"))
            totalPrice to null
        }

        if (totalCalculationFailure != null) return Left(totalCalculationFailure)

        val savePortfolioValueResult = portfolioRepository.updatePortfolioValue(total)
        savePortfolioValueResult.rightValue ?: return Left(savePortfolioValueResult.requireLeft())

        return Right(total)
    }

}