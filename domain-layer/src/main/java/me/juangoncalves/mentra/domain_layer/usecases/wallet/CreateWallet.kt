package me.juangoncalves.mentra.domain_layer.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain_layer.errors.OldFailure
import me.juangoncalves.mentra.domain_layer.models.Wallet
import me.juangoncalves.mentra.domain_layer.repositories.WalletRepository
import me.juangoncalves.mentra.domain_layer.usecases.OldUseCase
import me.juangoncalves.mentra.domain_layer.usecases.coin.DeterminePrimaryIcon
import javax.inject.Inject

class CreateWallet @Inject constructor(
    private val walletRepository: WalletRepository,
    private val determinePrimaryIcon: DeterminePrimaryIcon
) : OldUseCase<Wallet, Unit> {

    override suspend operator fun invoke(params: Wallet): Either<OldFailure, Unit> {
        TODO("Refactor with new Failure class")
//        determinePrimaryIcon(params.coin) // TODO: Handle failure
//        return walletRepository.createWallet(params)
    }

}