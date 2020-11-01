package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.extensions.Left
import me.juangoncalves.mentra.extensions.requireLeft
import me.juangoncalves.mentra.extensions.rightValue
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<DeleteWallet.Params, Unit> {

    class Params(val id: Long)

    override suspend fun invoke(params: Params): Either<Failure, Unit> {
        val findWalletResult = walletRepository.findWalletById(params.id)
        val wallet = findWalletResult.rightValue ?: return Left(findWalletResult.requireLeft())
        return walletRepository.deleteWallet(wallet)
    }

}