package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import javax.inject.Inject

class CreateWallet @Inject constructor(
    private val walletRepository: WalletRepository
) : UseCase<Wallet, Unit> {

    override suspend operator fun invoke(params: Wallet): Either<Failure, Unit> {
        return walletRepository.createWallet(params)
    }

}