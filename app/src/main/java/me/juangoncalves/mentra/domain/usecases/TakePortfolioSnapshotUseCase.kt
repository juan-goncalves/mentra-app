package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.extensions.*
import javax.inject.Inject

class TakePortfolioSnapshotUseCase @Inject constructor(
    private val getWallets: GetWalletsUseCase,
    private val refreshWalletValue: RefreshWalletValueUseCase
) {

    suspend operator fun invoke(): Either<Failure, Unit> {
        val getWalletsResult = getWallets()
        val wallets = getWalletsResult.rightValue ?: return Left(getWalletsResult.requireLeft())

        wallets.forEach { wallet ->
            val refreshResult = refreshWalletValue(wallet)
            if (refreshResult.isLeft()) return Left(refreshResult.requireLeft())
        }

        return Right(Unit)
    }

}