package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.OldUseCase
import javax.inject.Inject

class DeleteWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : OldUseCase<DeleteWallet.Params, Unit> {

    class Params(val id: Long)

    override suspend fun invoke(params: Params): Either<OldFailure, Unit> {
        TODO("Refactor to use the new Failure class")
//        val findWalletResult = walletRepository.findWalletById(params.id)
//        val wallet = findWalletResult.rightValue ?: return Left(findWalletResult.requireLeft())
//        return walletRepository.deleteWallet(wallet)
    }

}