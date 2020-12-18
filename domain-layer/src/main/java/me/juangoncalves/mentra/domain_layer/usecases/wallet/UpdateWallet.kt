package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.*
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.Interactor
import java.math.BigDecimal
import javax.inject.Inject

class UpdateWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : Interactor<UpdateWallet.Params, Unit> {

    class Params(val walletId: Long, val newAmount: BigDecimal)

    override suspend fun invoke(params: Params): Either<Failure, Unit> {
        val findWalletOp = walletRepository.findWalletById(params.walletId)
        if (findWalletOp.isLeft()) {
            return findWalletOp.requireLeft().toLeft()
        }

        val wallet = findWalletOp.requireRight() ?: return Right(Unit)
        val updates = wallet.copy(amount = params.newAmount)

        return walletRepository.updateWallet(updates)
    }

}