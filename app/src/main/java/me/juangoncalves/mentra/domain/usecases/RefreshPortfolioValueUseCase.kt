package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.extensions.*
import javax.inject.Inject

class RefreshPortfolioValueUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
    private val refreshWalletValue: RefreshWalletValueUseCase,
    private val portfolioRepository: PortfolioRepository
) {

    suspend operator fun invoke(): Either<Failure, Price> {
        val getWalletsResult = walletRepository.getWallets()
        val wallets = getWalletsResult.rightValue ?: return Left(getWalletsResult.requireLeft())

        val total = wallets.sumByDouble { wallet ->
            val refreshResult = refreshWalletValue(wallet)
            if (refreshResult.isLeft()) return Left(refreshResult.requireLeft())
            refreshResult.requireRight().value
        }.toPrice()

        val savePortfolioValueResult = portfolioRepository.updatePortfolioValue(total)
        savePortfolioValueResult.rightValue ?: return Left(savePortfolioValueResult.requireLeft())

        return Right(total)
    }

}