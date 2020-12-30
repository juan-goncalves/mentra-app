package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.extensions.Left
import me.juangoncalves.mentra.domain_layer.extensions.requireLeft
import me.juangoncalves.mentra.domain_layer.extensions.rightValue
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
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