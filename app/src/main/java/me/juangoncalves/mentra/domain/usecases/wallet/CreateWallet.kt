package me.juangoncalves.mentra.domain.usecases.wallet

import either.Either
import me.juangoncalves.mentra.domain.errors.Failure
import me.juangoncalves.mentra.domain.models.Wallet
import me.juangoncalves.mentra.domain.repositories.WalletRepository
import me.juangoncalves.mentra.domain.usecases.UseCase
import me.juangoncalves.mentra.domain.usecases.coin.DeterminePrimaryIcon
import javax.inject.Inject

class CreateWallet @Inject constructor(
    private val walletRepository: WalletRepository,
    private val determinePrimaryIcon: DeterminePrimaryIcon
) : UseCase<Wallet, Unit> {

    override suspend operator fun invoke(params: Wallet): Either<Failure, Unit> {
        determinePrimaryIcon(params.coin) // TODO: Handle failure
        return walletRepository.createWallet(params)
    }

}