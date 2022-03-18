package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.UseCase
import javax.inject.Inject

class CreateWallet @Inject constructor(
    private val walletRepository: WalletRepository,
) : UseCase<Wallet, Unit> {

    override suspend operator fun invoke(params: Wallet): Either<Failure, Unit> {
        return walletRepository.createWallet(params)
    }
}