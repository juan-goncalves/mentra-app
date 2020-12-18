package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.Failure
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.Interactor
import me.juangoncalves.mentra.domain_layer.usecases.coin.DeterminePrimaryIcon
import javax.inject.Inject

class CreateWallet @Inject constructor(
    private val walletRepository: WalletRepository,
    private val determinePrimaryIcon: DeterminePrimaryIcon
) : Interactor<Wallet, Unit> {

    override suspend operator fun invoke(params: Wallet): Either<Failure, Unit> {
        determinePrimaryIcon(params.coin)
        return walletRepository.createWallet(params)
    }

}