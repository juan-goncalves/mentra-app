package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.OldUseCase
import java.math.BigDecimal
import javax.inject.Inject

class UpdateWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : OldUseCase<UpdateWallet.Params, Unit> {

    class Params(val walletId: Long, val newAmount: BigDecimal)

    override suspend fun invoke(params: Params): Either<OldFailure, Unit> {
        TODO("Refactor to use new Failure class")
//        val result = walletRepository.findWalletById(params.walletId)
//        if (result.isLeft()) {
//            return Left(OldFailure()) // TODO: Return the proper failure after refactoring to new Failure class
//        }
//
//        val wallet = result.requireRight() ?: return Left(NotFoundFailure())
//        val updates = wallet.copy(amount = params.newAmount)
//
//        return walletRepository.updateWallet(updates)
    }

}