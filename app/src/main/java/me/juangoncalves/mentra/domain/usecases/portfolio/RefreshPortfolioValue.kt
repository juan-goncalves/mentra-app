package me.juangoncalves.mentra.domain.usecases.portfolio

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Price
import me.juangoncalves.mentra.domain.repositories.PortfolioRepository
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.VoidUseCase
import me.juangoncalves.mentra.domain.usecases.wallet.RefreshWalletValue
import me.juangoncalves.mentra.extensions.*
import javax.inject.Inject

class RefreshPortfolioValue @Inject constructor(
    private val walletRepository: WalletRepository,
    private val portfolioRepository: PortfolioRepository,
    private val refreshWalletValue: RefreshWalletValue
) : VoidUseCase<Price> {

    override suspend operator fun invoke(): Either<Failure, Price> {
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