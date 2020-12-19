package me.juangoncalves.mentra.domain_layer.usecases.portfolio

import either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.extensions.*
import me.juangoncalves.mentra.domain_layer.models.Price
import me.juangoncalves.mentra.domain_layer.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.VoidOldUseCase
import me.juangoncalves.mentra.domain_layer.usecases.wallet.RefreshWalletValue
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class RefreshPortfolioValue @Inject constructor(
    private val walletRepository: WalletRepository,
    private val portfolioRepository: PortfolioRepository,
    private val refreshWalletValue: RefreshWalletValue
) : VoidOldUseCase<Price> {

    override suspend operator fun invoke(): Either<OldFailure, Price> {
        val getWalletsResult = walletRepository.getWallets()
        val wallets = getWalletsResult.rightValue
            ?: return Left(OldFailure())
        // TODO: Use the failure from getWalletsResult after its refactored

        val (total, totalCalculationFailure) = withContext(Dispatchers.Default) {
            var total = BigDecimal.ZERO
            for (wallet in wallets) {
                val refreshResult = refreshWalletValue(wallet)
                if (refreshResult.isLeft()) {
                    return@withContext Pair<Price, OldFailure>(
                        Price.None,
                        OldFailure() // TODO: Use the failure from refreshResult after its refactored
                    )
                }
                total += refreshResult.requireRight().value
            }

            val totalPrice = total.toPrice(currency = Currency.getInstance("USD"))
            totalPrice to null
        }

        if (totalCalculationFailure != null) return Left(totalCalculationFailure)

        val savePortfolioValueResult = portfolioRepository.updatePortfolioValue(total)
        // TODO: Use the failure from savePortfolioValueResult after its refactored
        savePortfolioValueResult.rightValue ?: return OldFailure().toLeft()

        return Right(total)
    }

}