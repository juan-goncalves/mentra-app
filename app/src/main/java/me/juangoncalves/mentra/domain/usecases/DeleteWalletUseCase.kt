package me.juangoncalves.mentra.domain.usecases

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import javax.inject.Inject

class DeleteWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {

    suspend operator fun invoke(wallet: Wallet): Either<Failure, Unit> =
        walletRepository.deleteWallet(wallet)

}