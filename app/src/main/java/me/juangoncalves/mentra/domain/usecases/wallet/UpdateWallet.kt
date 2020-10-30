package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.errors.NotFoundFailure
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.isLeft
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.requireRight
import javax.inject.Inject

class UpdateWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<UpdateWallet.Params, Unit> {

    class Params(val walletId: Long, val newAmount: Double)

    override suspend fun invoke(params: Params): Either<Failure, Unit> {
        val result = walletRepository.findWalletById(params.walletId)
        if (result.isLeft()) return Left(result.requireLeft())

        val wallet = result.requireRight() ?: return Left(NotFoundFailure())
        val updates = wallet.copy(amount = params.newAmount)

        return walletRepository.updateWallet(updates)
    }

}